package com.deepu.glyphtext.engine

/**
 * PixelFont — Custom 5×5 pixel font for Glyph Matrix rendering.
 *
 * Each character is defined as a list of IntArrays (rows), where:
 *   1 = LED on (pixel lit)
 *   0 = LED off (pixel dark)
 *
 * Most characters are 5 columns wide. Some narrow characters (I, 1, !, ., etc.)
 * are 3 columns wide. Space is 3 columns of blank.
 *
 * Font height is always 5 rows.
 */
object PixelFont {

    /** Height of every glyph in pixels */
    const val GLYPH_HEIGHT = 5

    /** Spacing between characters in pixels */
    const val CHAR_SPACING = 1

    /**
     * Returns the pixel glyph for a given character.
     * Unknown characters return a 5×5 filled block (█).
     */
    fun getGlyph(char: Char): List<IntArray> {
        return FONT_MAP[char.uppercaseChar()] ?: UNKNOWN_GLYPH
    }

    /**
     * Returns the width (columns) of a character's glyph.
     */
    fun getGlyphWidth(char: Char): Int {
        return getGlyph(char)[0].size
    }

    // ── Fallback: solid 5×5 block for unknown characters ──
    private val UNKNOWN_GLYPH = listOf(
        intArrayOf(1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1)
    )

    /**
     * Complete font map: A-Z, 0-9, and common punctuation.
     * Each glyph is 5 rows tall. Width varies by character.
     */
    private val FONT_MAP: Map<Char, List<IntArray>> = mapOf(

        // ────────────── LETTERS A-Z ──────────────

        'A' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'B' to listOf(
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0)
        ),
        'C' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        'D' to listOf(
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0)
        ),
        'E' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 1)
        ),
        'F' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0)
        ),
        'G' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        'H' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'I' to listOf(
            intArrayOf(1, 1, 1),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1)
        ),
        'J' to listOf(
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        'K' to listOf(
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 1, 0, 0),
            intArrayOf(1, 1, 0, 0, 0),
            intArrayOf(1, 0, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0)
        ),
        'L' to listOf(
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 1)
        ),
        'M' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 0, 1, 1),
            intArrayOf(1, 0, 1, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'N' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 0, 0, 1),
            intArrayOf(1, 0, 1, 0, 1),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'O' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        'P' to listOf(
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0)
        ),
        'Q' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 1, 0, 1),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(0, 1, 1, 0, 1)
        ),
        'R' to listOf(
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 1, 0, 0),
            intArrayOf(1, 0, 0, 1, 0)
        ),
        'S' to listOf(
            intArrayOf(0, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0)
        ),
        'T' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        'U' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        'V' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        'W' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 1, 0, 1),
            intArrayOf(1, 1, 0, 1, 1),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'X' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(1, 0, 0, 0, 1)
        ),
        'Y' to listOf(
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        'Z' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 1)
        ),

        // ────────────── DIGITS 0-9 ──────────────

        '0' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 1, 1),
            intArrayOf(1, 0, 1, 0, 1),
            intArrayOf(1, 1, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        '1' to listOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1)
        ),
        '2' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 0, 1, 1, 0),
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 1)
        ),
        '3' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        '4' to listOf(
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 0, 0, 1, 0),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 1, 0)
        ),
        '5' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 0)
        ),
        '6' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        '7' to listOf(
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        '8' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        '9' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 1),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 1, 1, 1, 0)
        ),

        // ────────────── PUNCTUATION & SPECIAL ──────────────

        ' ' to listOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        '.' to listOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0)
        ),
        ',' to listOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(1, 0, 0)
        ),
        '!' to listOf(
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0)
        ),
        '?' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(0, 0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        '-' to listOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0)
        ),
        ':' to listOf(
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 0)
        ),
        '\'' to listOf(
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0)
        ),
        '+' to listOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 0, 0, 0)
        ),
        '=' to listOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0, 0)
        ),
        '#' to listOf(
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(1, 1, 1, 1, 1),
            intArrayOf(0, 1, 0, 1, 0)
        ),
        '@' to listOf(
            intArrayOf(0, 1, 1, 1, 0),
            intArrayOf(1, 0, 0, 0, 1),
            intArrayOf(1, 0, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 1, 1, 1, 0)
        ),
        '*' to listOf(
            intArrayOf(0, 0, 0, 0, 0),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 0)
        ),
        '/' to listOf(
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0)
        ),
        '(' to listOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 0, 0),
            intArrayOf(1, 0, 0),
            intArrayOf(1, 0, 0),
            intArrayOf(0, 1, 0)
        ),
        ')' to listOf(
            intArrayOf(0, 1, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 1, 0)
        ),
        '<' to listOf(
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(1, 0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0, 0),
            intArrayOf(0, 0, 1, 0, 0)
        ),
        '>' to listOf(
            intArrayOf(0, 0, 1, 0, 0),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 0, 0, 1),
            intArrayOf(0, 0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0)
        )
    )
}
