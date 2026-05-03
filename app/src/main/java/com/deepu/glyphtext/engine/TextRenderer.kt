package com.deepu.glyphtext.engine

/**
 * TextRenderer — Converts a text string into a wide horizontal bitmap.
 *
 * The output bitmap has a fixed height of [MATRIX_SIZE] rows (13 for Phone 4a Pro).
 * The 5-row character glyphs are vertically centered in the bitmap.
 * Each character is separated by [PixelFont.CHAR_SPACING] pixel columns.
 *
 * Output format: Array<IntArray> where result[y][x] = brightness value (0 or given brightness).
 */
object TextRenderer {

    /** Matrix height — characters are centered vertically within this */
    const val MATRIX_SIZE = 13

    /**
     * Renders the given text string into a horizontal pixel bitmap.
     *
     * @param text The text to render (converted to uppercase internally).
     * @param brightness LED brightness for lit pixels (0–255). Default is 255.
     * @return A 2D array [y][x] of brightness values. Height is always [MATRIX_SIZE].
     *         Width depends on the text length and character widths.
     *         Returns a minimal empty bitmap if text is blank.
     */
    fun renderText(text: String, brightness: Int = 255): Array<IntArray> {
        val upperText = text.uppercase()

        // If text is empty, return a small blank bitmap
        if (upperText.isEmpty()) {
            return Array(MATRIX_SIZE) { IntArray(1) { 0 } }
        }

        // ── Step 1: Calculate total width ──
        var totalWidth = 0
        for (i in upperText.indices) {
            totalWidth += PixelFont.getGlyphWidth(upperText[i])
            if (i < upperText.length - 1) {
                totalWidth += PixelFont.CHAR_SPACING
            }
        }

        // ── Step 2: Create the bitmap ──
        val bitmap = Array(MATRIX_SIZE) { IntArray(totalWidth) { 0 } }

        // ── Step 3: Render each character ──
        // Vertically center the 5-row glyphs in the 13-row bitmap
        val verticalOffset = (MATRIX_SIZE - PixelFont.GLYPH_HEIGHT) / 2

        var cursorX = 0
        for (char in upperText) {
            val glyph = PixelFont.getGlyph(char)
            val glyphWidth = glyph[0].size

            // Copy glyph pixels into the bitmap
            for (glyphRow in 0 until PixelFont.GLYPH_HEIGHT) {
                val bitmapRow = verticalOffset + glyphRow
                for (glyphCol in 0 until glyphWidth) {
                    if (glyph[glyphRow][glyphCol] == 1) {
                        bitmap[bitmapRow][cursorX + glyphCol] = brightness
                    }
                }
            }

            cursorX += glyphWidth + PixelFont.CHAR_SPACING
        }

        return bitmap
    }

    /**
     * Calculates the total pixel width needed for the given text.
     * Useful for UI previews without generating the full bitmap.
     */
    fun calculateTextWidth(text: String): Int {
        val upperText = text.uppercase()
        if (upperText.isEmpty()) return 0

        var totalWidth = 0
        for (i in upperText.indices) {
            totalWidth += PixelFont.getGlyphWidth(upperText[i])
            if (i < upperText.length - 1) {
                totalWidth += PixelFont.CHAR_SPACING
            }
        }
        return totalWidth
    }
}
