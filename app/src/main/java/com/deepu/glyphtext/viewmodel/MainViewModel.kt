package com.deepu.glyphtext.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.engine.TextRenderer
import com.deepu.glyphtext.glyph.GlyphController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainViewModel — Coordinates text rendering, frame generation, animation playback,
 * and Glyph SDK interaction.
 *
 * Exposes observable state for the UI:
 *   - [inputText]: Current text input
 *   - [animationSpeed]: Delay between frames in ms
 *   - [animationType]: Selected animation style
 *   - [isPlaying]: Whether animation is currently running
 *   - [currentFrame]: The frame currently displayed (for the simulator)
 *   - [statusMessage]: Human-readable status text
 *   - [frameCount]: Total frames in current animation
 *   - [currentFrameIndex]: Current frame number during playback
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
        private const val DEFAULT_SPEED_MS = 100L
        private const val MIN_SPEED_MS = 30L
        private const val MAX_SPEED_MS = 500L
    }

    // ── Glyph SDK Controller ──
    private val glyphController = GlyphController()

    // ── User Input State ──
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _animationSpeed = MutableStateFlow(DEFAULT_SPEED_MS)
    val animationSpeed: StateFlow<Long> = _animationSpeed.asStateFlow()

    private val _animationType = MutableStateFlow(FrameGenerator.AnimationType.SCROLL_LEFT)
    val animationType: StateFlow<FrameGenerator.AnimationType> = _animationType.asStateFlow()

    // ── Playback State ──
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentFrame = MutableStateFlow(FrameGenerator.blankFrame())
    val currentFrame: StateFlow<IntArray> = _currentFrame.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _frameCount = MutableStateFlow(0)
    val frameCount: StateFlow<Int> = _frameCount.asStateFlow()

    private val _currentFrameIndex = MutableStateFlow(0)
    val currentFrameIndex: StateFlow<Int> = _currentFrameIndex.asStateFlow()

    // ── Glyph Connection State (exposed from controller) ──
    val isGlyphConnected: StateFlow<Boolean> = glyphController.isConnected

    // ── Internal ──
    private var animationJob: Job? = null

    init {
        // Initialize Glyph SDK with application context
        glyphController.init(application.applicationContext)
    }

    // ── Public API ──

    /**
     * Update the text input.
     */
    fun updateText(text: String) {
        _inputText.value = text
    }

    /**
     * Update the animation speed (delay between frames in milliseconds).
     */
    fun updateSpeed(speedMs: Long) {
        _animationSpeed.value = speedMs.coerceIn(MIN_SPEED_MS, MAX_SPEED_MS)
    }

    /**
     * Toggle animation type between SCROLL_LEFT and TYPING.
     */
    fun updateAnimationType(type: FrameGenerator.AnimationType) {
        _animationType.value = type
    }

    /**
     * Start the animation playback.
     *
     * Pipeline:
     *   1. Render text → bitmap (TextRenderer)
     *   2. Generate frames from bitmap (FrameGenerator)
     *   3. Loop through frames, pushing to both simulator and Glyph hardware
     */
    fun playAnimation() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) {
            _statusMessage.value = "Enter text to animate"
            return
        }

        // Cancel any existing animation
        stopAnimation()

        animationJob = viewModelScope.launch {
            _isPlaying.value = true
            _statusMessage.value = "Generating frames..."

            try {
                // Step 1 & 2: Render text and generate frames on background thread
                val frames = withContext(Dispatchers.Default) {
                    Log.d(TAG, "Rendering text: '$text'")
                    val bitmap = TextRenderer.renderText(text)
                    Log.d(TAG, "Bitmap size: ${bitmap.size} x ${bitmap[0].size}")

                    val type = _animationType.value
                    Log.d(TAG, "Generating ${type.name} frames...")
                    FrameGenerator.generateFrames(bitmap, type)
                }

                _frameCount.value = frames.size
                Log.d(TAG, "Generated ${frames.size} frames")
                _statusMessage.value = "Playing (${frames.size} frames)..."

                // Step 3: Playback loop
                var loopCount = 0
                while (isActive) {
                    for ((index, frame) in frames.withIndex()) {
                        if (!isActive) break

                        _currentFrameIndex.value = index
                        _currentFrame.value = frame

                        // Push to real Glyph hardware (no-op if not connected)
                        glyphController.displayFrame(frame)

                        delay(_animationSpeed.value)
                    }
                    loopCount++
                    Log.d(TAG, "Animation loop $loopCount completed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Animation error: ${e.message}", e)
                _statusMessage.value = "Error: ${e.message}"
            } finally {
                _isPlaying.value = false
                _statusMessage.value = "Stopped"
            }
        }
    }

    /**
     * Stop the current animation.
     */
    fun stopAnimation() {
        animationJob?.cancel()
        animationJob = null
        _isPlaying.value = false
        _currentFrameIndex.value = 0
        _statusMessage.value = "Ready"

        // Clear the Glyph display
        _currentFrame.value = FrameGenerator.blankFrame()
        glyphController.clearDisplay()
    }

    /**
     * Cleanup when ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared — releasing resources")
        animationJob?.cancel()
        glyphController.close()
    }
}
