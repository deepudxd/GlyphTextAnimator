package com.deepu.glyphtext.glyph

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.engine.TextRenderer
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * TextGlyphToyService — Glyph Toy bound service for Always-On Display (AOD).
 *
 * Implements the Nothing Glyph Toy protocol using Messenger/Handler pattern
 * (matching the pattern used by Screenie and other working Glyph Toys).
 *
 * Key design (learned from Screenie):
 *   - Returns messenger.binder from onBind()
 *   - Starts animation IMMEDIATELY when GlyphMatrixManager connects
 *     (does NOT wait for messenger lifecycle events — avoids race conditions)
 *   - Handles toy lifecycle messages as start/stop signals
 *   - Uses setMatrixFrame() (NOT setAppMatrixFrame)
 */
class TextGlyphToyService : Service() {

    companion object {
        private const val TAG = "TextGlyphToyService"

        // Glyph Toy messenger protocol constants.
        // Value 1 matches GlyphToy.MSG_GLYPH_TOY from newer SDKs.
        private const val MSG_GLYPH_TOY = 1
    }

    private var glyphMatrixManager: GlyphMatrixManager? = null
    private var serviceScope: CoroutineScope? = null
    private var animationJob: Job? = null

    /**
     * Messenger handler — receives lifecycle events from the Nothing Glyph Toy framework.
     * We handle these as start/stop signals, but the animation also auto-starts
     * on SDK connect (like Screenie does) to avoid missing events.
     */
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_GLYPH_TOY -> {
                    val data = msg.data ?: return
                    // Try all known key names for the event data
                    val event = data.getString("event")
                        ?: data.getString("data")
                        ?: data.getString("com.nothing.glyph.toy.data")
                        ?: run {
                            // Log all keys so we can debug if needed
                            Log.d(TAG, "Toy message keys: ${data.keySet()}")
                            data.keySet().firstOrNull()?.let { data.getString(it) }
                        }
                    Log.d(TAG, "Toy event: $event")
                    when (event) {
                        "prepare" -> onPrepare()
                        "start" -> onToyStart()
                        "end" -> onToyEnd()
                        "aod" -> onAod()
                        else -> Log.d(TAG, "Unknown toy event: $event")
                    }
                }
                else -> {
                    Log.d(TAG, "Unknown message what=${msg.what}")
                    super.handleMessage(msg)
                }
            }
        }
    }

    /** Messenger binder — returned from onBind() for the Glyph Toy protocol */
    private val messenger = Messenger(handler)

    /** SDK initialization callback — starts animation immediately on connect */
    private val gmmCallback = object : GlyphMatrixManager.Callback {
        override fun onServiceConnected(componentName: ComponentName?) {
            Log.d(TAG, "Glyph service connected for Toy mode")
            try {
                glyphMatrixManager?.register(Glyph.DEVICE_25111p)
                Log.d(TAG, "Registered device: Glyph.DEVICE_25111p")

                // Start animation IMMEDIATELY on connect (like Screenie does).
                // Don't wait for messenger STATUS_START — it may arrive before
                // this callback, or the message key might not match our constants.
                initAndStartAnimation()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register device: ${e.message}", e)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Log.d(TAG, "Glyph service disconnected")
            stopAnimation()
        }
    }

    // ── Service Lifecycle ──

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind — initializing Glyph Toy")

        // Create a fresh coroutine scope for this bind session
        serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        // Initialize GlyphMatrixManager
        glyphMatrixManager = GlyphMatrixManager.getInstance(applicationContext)?.also {
            it.init(gmmCallback)
            Log.d(TAG, "GlyphMatrixManager initialized")
        } ?: run {
            Log.w(TAG, "GlyphMatrixManager.getInstance() returned null")
            null
        }

        // Return the Messenger binder — this is the Glyph Toy protocol
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind — cleaning up Glyph Toy")
        cleanup()
        return false
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        cleanup()
        super.onDestroy()
    }

    // ── Glyph Toy Lifecycle Events ──

    private fun onPrepare() {
        Log.d(TAG, "Toy prepare")
    }

    private fun onToyStart() {
        Log.d(TAG, "Toy start — ensuring animation is running")
        // If animation isn't already running (e.g. it was stopped by onToyEnd),
        // restart it.
        if (animationJob == null || animationJob?.isActive != true) {
            initAndStartAnimation()
        }
    }

    private fun onToyEnd() {
        Log.d(TAG, "Toy end — stopping animation")
        stopAnimation()
        try {
            glyphMatrixManager?.setMatrixFrame(FrameGenerator.blankFrame())
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing display on toy end: ${e.message}", e)
        }
    }

    private fun onAod() {
        Log.d(TAG, "AOD event")
    }

    // ── Animation Logic ──

    /**
     * Load saved text, generate frames, and immediately start the animation loop.
     * This is called directly from onServiceConnected (like Screenie's initScreenie + startScreenie).
     * Runs everything in a single coroutine to avoid race conditions.
     */
    private fun initAndStartAnimation() {
        // Cancel any existing animation
        animationJob?.cancel()

        val scope = serviceScope ?: run {
            Log.e(TAG, "No service scope available")
            return
        }

        animationJob = scope.launch {
            try {
                // Load settings from SharedPreferences
                val text = GlyphToyPrefs.loadText(applicationContext)
                val speedMs = GlyphToyPrefs.loadSpeed(applicationContext)
                val animationType = GlyphToyPrefs.loadAnimationType(applicationContext)

                Log.d(TAG, "Loaded prefs: text='$text', speed=${speedMs}ms, type=$animationType")

                if (text.isBlank()) {
                    Log.w(TAG, "No text saved — displaying blank frame. " +
                            "User needs to type text and tap 'Set as Glyph Toy' first.")
                    try {
                        glyphMatrixManager?.setMatrixFrame(FrameGenerator.blankFrame())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error displaying blank: ${e.message}", e)
                    }
                    return@launch
                }

                // Generate frames (already in Dispatchers.Default)
                Log.d(TAG, "Generating frames for: '$text'")
                val bitmap = TextRenderer.renderText(text)
                val frames = FrameGenerator.generateFrames(bitmap, animationType)
                Log.d(TAG, "Generated ${frames.size} frames")

                if (frames.isEmpty()) {
                    Log.w(TAG, "No frames generated")
                    return@launch
                }

                // Animation loop — starts IMMEDIATELY, cycles continuously
                Log.d(TAG, "Starting animation loop (${frames.size} frames, ${speedMs}ms delay)")
                var loopCount = 0
                while (isActive) {
                    for (frame in frames) {
                        if (!isActive) break
                        try {
                            glyphMatrixManager?.setMatrixFrame(frame)
                        } catch (e: Exception) {
                            Log.e(TAG, "setMatrixFrame error: ${e.message}", e)
                        }
                        delay(speedMs)
                    }
                    loopCount++
                    if (loopCount % 50 == 0) {
                        Log.d(TAG, "Animation loop #$loopCount")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Animation error: ${e.message}", e)
            }
        }
    }

    /** Stop the animation loop. */
    private fun stopAnimation() {
        animationJob?.cancel()
        animationJob = null
    }

    /** Full cleanup: stop animation, clear display, uninit SDK, cancel scope. */
    private fun cleanup() {
        stopAnimation()

        try {
            glyphMatrixManager?.setMatrixFrame(FrameGenerator.blankFrame())
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing display: ${e.message}", e)
        }

        try {
            glyphMatrixManager?.unInit()
        } catch (e: Exception) {
            Log.e(TAG, "Error during unInit: ${e.message}", e)
        }

        glyphMatrixManager = null
        serviceScope?.cancel()
        serviceScope = null
    }
}
