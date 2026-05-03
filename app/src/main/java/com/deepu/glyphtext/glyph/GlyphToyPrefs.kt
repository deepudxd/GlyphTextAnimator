package com.deepu.glyphtext.glyph

import android.content.Context
import com.deepu.glyphtext.engine.FrameGenerator

/**
 * GlyphToyPrefs — Centralized SharedPreferences access for Glyph Toy settings.
 *
 * Provides a bridge between the main Activity (which writes user settings)
 * and the TextGlyphToyService (which reads them during AOD playback).
 *
 * Stored keys:
 *   - [KEY_TEXT]: The user's text input
 *   - [KEY_SPEED_MS]: Animation speed in milliseconds
 *   - [KEY_ANIMATION_TYPE]: Animation type ordinal (0 = SCROLL_LEFT, 1 = TYPING)
 */
object GlyphToyPrefs {

    private const val PREFS_NAME = "glyph_toy_prefs"
    private const val KEY_TEXT = "toy_text"
    private const val KEY_SPEED_MS = "toy_speed_ms"
    private const val KEY_ANIMATION_TYPE = "toy_animation_type"

    private const val DEFAULT_SPEED_MS = 100L

    // ── Write Methods (called from Activity/ViewModel) ──

    /**
     * Save the user's text input for Glyph Toy playback.
     */
    fun saveText(context: Context, text: String) {
        prefs(context).edit().putString(KEY_TEXT, text).apply()
    }

    /**
     * Save the animation speed (delay between frames in ms).
     */
    fun saveSpeed(context: Context, speedMs: Long) {
        prefs(context).edit().putLong(KEY_SPEED_MS, speedMs).apply()
    }

    /**
     * Save the animation type (SCROLL_LEFT or TYPING).
     */
    fun saveAnimationType(context: Context, type: FrameGenerator.AnimationType) {
        prefs(context).edit().putInt(KEY_ANIMATION_TYPE, type.ordinal).apply()
    }

    /**
     * Save all Glyph Toy settings in a single transaction.
     */
    fun saveAll(
        context: Context,
        text: String,
        speedMs: Long,
        animationType: FrameGenerator.AnimationType
    ) {
        prefs(context).edit()
            .putString(KEY_TEXT, text)
            .putLong(KEY_SPEED_MS, speedMs)
            .putInt(KEY_ANIMATION_TYPE, animationType.ordinal)
            .apply()
    }

    // ── Read Methods (called from TextGlyphToyService) ──

    /**
     * Load the saved text. Returns empty string if not set.
     */
    fun loadText(context: Context): String {
        return prefs(context).getString(KEY_TEXT, "") ?: ""
    }

    /**
     * Load the saved animation speed. Returns [DEFAULT_SPEED_MS] if not set.
     */
    fun loadSpeed(context: Context): Long {
        return prefs(context).getLong(KEY_SPEED_MS, DEFAULT_SPEED_MS)
    }

    /**
     * Load the saved animation type. Returns SCROLL_LEFT if not set or invalid.
     */
    fun loadAnimationType(context: Context): FrameGenerator.AnimationType {
        val ordinal = prefs(context).getInt(KEY_ANIMATION_TYPE, 0)
        return FrameGenerator.AnimationType.entries.getOrElse(ordinal) {
            FrameGenerator.AnimationType.SCROLL_LEFT
        }
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
