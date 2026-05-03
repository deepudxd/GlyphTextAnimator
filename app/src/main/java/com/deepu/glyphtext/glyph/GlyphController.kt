package com.deepu.glyphtext.glyph

import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * GlyphController — Wraps the Nothing Glyph Matrix SDK lifecycle.
 *
 * Manages the GlyphMatrixManager connection, device registration, and frame display.
 * Uses [setAppMatrixFrame] for app-based control (not Glyph Toy service mode).
 *
 * This class is designed to be held by a ViewModel and survives configuration changes.
 * Call [init] on creation, [displayFrame] to push frames, and [close] on cleanup.
 *
 * If the SDK is unavailable (not a Nothing phone, SDK missing, etc.), all operations
 * are no-ops and [isConnected] remains false.
 */
class GlyphController {

    companion object {
        private const val TAG = "GlyphController"

        /** Target device: Nothing Phone (4a) Pro */
        private const val TARGET_DEVICE = "Glyph.DEVICE_25111p"
    }

    private var glyphMatrixManager: GlyphMatrixManager? = null
    private var isInitialized = false

    /** Observable connection state for UI */
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    /** SDK initialization callback */
    private val gmmCallback = object : GlyphMatrixManager.Callback {
        override fun onServiceConnected(componentName: ComponentName?) {
            Log.d(TAG, "Glyph service connected")
            try {
                glyphMatrixManager?.register(Glyph.DEVICE_25111p)
                _isConnected.value = true
                Log.d(TAG, "Registered for device: ${TARGET_DEVICE}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register device: ${e.message}", e)
                _isConnected.value = false
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            Log.d(TAG, "Glyph service disconnected")
            _isConnected.value = false
        }
    }

    /**
     * Initialize the Glyph Matrix SDK.
     * Must be called with an application context (not activity context).
     *
     * @param context Application context for SDK initialization.
     */
    fun init(context: Context) {
        if (isInitialized) {
            Log.w(TAG, "Already initialized, skipping")
            return
        }

        try {
            val gmm = GlyphMatrixManager.getInstance(context.applicationContext)
            if (gmm != null) {
                glyphMatrixManager = gmm
                gmm.init(gmmCallback)
                isInitialized = true
                Log.d(TAG, "GlyphMatrixManager initialized successfully")
            } else {
                Log.w(TAG, "GlyphMatrixManager.getInstance() returned null — " +
                        "this device may not support Glyph Matrix")
            }
        } catch (e: Exception) {
            // Gracefully handle: SDK not available on this device
            Log.w(TAG, "Glyph Matrix SDK not available: ${e.message}")
            glyphMatrixManager = null
            isInitialized = false
        }
    }

    /**
     * Push a single frame to the Glyph Matrix hardware.
     *
     * @param frame IntArray of size 169 (13×13), where each value is 0–255 brightness.
     *              Index mapping: index = y * 13 + x
     */
    fun displayFrame(frame: IntArray) {
        if (!_isConnected.value) return

        try {
            glyphMatrixManager?.setAppMatrixFrame(frame)
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying frame: ${e.message}", e)
        }
    }

    /**
     * Turn off all LEDs on the Glyph Matrix.
     */
    fun clearDisplay() {
        if (!_isConnected.value) return

        try {
            glyphMatrixManager?.setAppMatrixFrame(IntArray(169) { 0 })
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing display: ${e.message}", e)
        }
    }

    /**
     * Release SDK resources. Call this when the ViewModel is cleared.
     */
    fun close() {
        Log.d(TAG, "Closing GlyphController")
        try {
            if (_isConnected.value) {
                glyphMatrixManager?.closeAppMatrix()
            }
            glyphMatrixManager?.unInit()
        } catch (e: Exception) {
            Log.e(TAG, "Error during close: ${e.message}", e)
        } finally {
            glyphMatrixManager = null
            isInitialized = false
            _isConnected.value = false
        }
    }
}
