package com.deepu.glyphtext.engine

/**
 * FrameGenerator — Converts a wide horizontal bitmap into animation frames.
 *
 * Each frame is a flattened IntArray of size [MATRIX_SIZE] × [MATRIX_SIZE] (169 for 13×13).
 * Pixel indexing follows the Glyph Matrix convention: index = y * MATRIX_SIZE + x.
 *
 * Supports two animation types:
 *   - SCROLL_LEFT: Slides a window from right to left across the text
 *   - TYPING: Characters appear column by column from the left
 */
object FrameGenerator {

    /** Matrix dimension (13×13 for Phone 4a Pro) */
    const val MATRIX_SIZE = 13

    /** Total LED count per frame */
    const val FRAME_SIZE = MATRIX_SIZE * MATRIX_SIZE // 169

    /**
     * Animation type enum.
     */
    enum class AnimationType {
        /** Text scrolls smoothly from right to left */
        SCROLL_LEFT,
        /** Characters appear one column at a time, typing effect */
        TYPING
    }

    /**
     * Generates all animation frames for the given bitmap and animation type.
     *
     * @param bitmap The wide horizontal bitmap from TextRenderer (height = MATRIX_SIZE).
     * @param animationType The animation style to use.
     * @return A list of IntArray frames (each of size [FRAME_SIZE]).
     *         All frames are precomputed for smooth playback.
     */
    fun generateFrames(
        bitmap: Array<IntArray>,
        animationType: AnimationType
    ): List<IntArray> {
        return when (animationType) {
            AnimationType.SCROLL_LEFT -> generateScrollFrames(bitmap)
            AnimationType.TYPING -> generateTypingFrames(bitmap)
        }
    }

    /**
     * SCROLL_LEFT animation:
     * Creates a padded canvas with [MATRIX_SIZE] blank columns on each side.
     * Slides a [MATRIX_SIZE]×[MATRIX_SIZE] window from left to right through the padded canvas.
     * This makes the text scroll in from the right and exit to the left.
     */
    private fun generateScrollFrames(bitmap: Array<IntArray>): List<IntArray> {
        val bitmapWidth = if (bitmap.isNotEmpty()) bitmap[0].size else 0

        // Add MATRIX_SIZE blank padding on left and right for smooth entry/exit
        val paddedWidth = bitmapWidth + MATRIX_SIZE * 2
        val paddedBitmap = Array(MATRIX_SIZE) { y ->
            IntArray(paddedWidth) { x ->
                val sourceX = x - MATRIX_SIZE
                if (sourceX in 0 until bitmapWidth) {
                    bitmap[y][sourceX]
                } else {
                    0
                }
            }
        }

        // Generate frames by sliding the window
        val totalFrames = paddedWidth - MATRIX_SIZE + 1
        val frames = mutableListOf<IntArray>()

        for (frameIndex in 0 until totalFrames) {
            val frame = IntArray(FRAME_SIZE)
            for (y in 0 until MATRIX_SIZE) {
                for (x in 0 until MATRIX_SIZE) {
                    val sourceX = frameIndex + x
                    if (sourceX < paddedWidth) {
                        frame[y * MATRIX_SIZE + x] = paddedBitmap[y][sourceX]
                    }
                }
            }
            frames.add(frame)
        }

        return frames
    }

    /**
     * TYPING animation:
     * Characters appear one column at a time from the left.
     * Once the 13-column window is filled, the view shifts to accommodate new columns,
     * creating a typewriter-like effect.
     */
    private fun generateTypingFrames(bitmap: Array<IntArray>): List<IntArray> {
        val bitmapWidth = if (bitmap.isNotEmpty()) bitmap[0].size else 0
        val frames = mutableListOf<IntArray>()

        for (col in 1..bitmapWidth) {
            val frame = IntArray(FRAME_SIZE)

            // Determine the visible window
            // If the text so far fits within MATRIX_SIZE columns, show from column 0
            // Otherwise, show the rightmost MATRIX_SIZE columns
            val startX = if (col <= MATRIX_SIZE) 0 else col - MATRIX_SIZE

            for (y in 0 until MATRIX_SIZE) {
                for (x in 0 until MATRIX_SIZE) {
                    val sourceX = startX + x
                    if (sourceX < col && sourceX < bitmapWidth) {
                        frame[y * MATRIX_SIZE + x] = bitmap[y][sourceX]
                    }
                }
            }
            frames.add(frame)
        }

        // If no frames were generated (empty text), add one blank frame
        if (frames.isEmpty()) {
            frames.add(IntArray(FRAME_SIZE))
        }

        return frames
    }

    /**
     * Creates a single blank frame (all LEDs off).
     * Useful for clearing the display.
     */
    fun blankFrame(): IntArray = IntArray(FRAME_SIZE)
}
