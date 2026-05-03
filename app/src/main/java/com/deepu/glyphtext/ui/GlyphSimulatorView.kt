package com.deepu.glyphtext.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.ui.theme.LedGlow
import com.deepu.glyphtext.ui.theme.LedOff
import com.deepu.glyphtext.ui.theme.LedOn
import com.deepu.glyphtext.ui.theme.NothingBlack
import com.deepu.glyphtext.ui.theme.NothingGray

/**
 * GlyphSimulatorView — Visual 13×13 LED matrix simulator with circular dots.
 *
 * Renders a grid of circular dots representing the Glyph Matrix LEDs.
 * Each cell maps to frame[y * 13 + x], with brightness controlling the
 * white intensity and glow effect.
 *
 * This composable updates in real-time as frames change, providing a
 * faithful on-screen preview of what the physical Glyph Matrix displays.
 */
@Composable
fun GlyphSimulatorView(
    frame: IntArray,
    modifier: Modifier = Modifier
) {
    val matrixSize = FrameGenerator.MATRIX_SIZE

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .aspectRatio(1f)
            .border(
                width = 1.dp,
                color = NothingGray,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = NothingBlack,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val gridSize = size.width
            val cellSize = gridSize / matrixSize
            val dotRadius = cellSize * 0.35f

            for (y in 0 until matrixSize) {
                for (x in 0 until matrixSize) {
                    val index = y * matrixSize + x
                    val brightness = if (index < frame.size) {
                        frame[index].coerceIn(0, 255)
                    } else {
                        0
                    }

                    val centerX = x * cellSize + cellSize / 2f
                    val centerY = y * cellSize + cellSize / 2f

                    drawLedDot(
                        centerX = centerX,
                        centerY = centerY,
                        radius = dotRadius,
                        brightness = brightness
                    )
                }
            }
        }
    }
}

/**
 * Draws a single circular LED dot with optional glow effect.
 */
private fun DrawScope.drawLedDot(
    centerX: Float,
    centerY: Float,
    radius: Float,
    brightness: Int
) {
    val alpha = brightness / 255f
    val center = Offset(centerX, centerY)

    if (brightness == 0) {
        // LED off — dim dot
        drawCircle(
            color = LedOff,
            radius = radius,
            center = center
        )
    } else {
        // Glow layer — soft radial gradient behind the LED
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    LedGlow.copy(alpha = alpha * 0.5f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 2.2f
            ),
            radius = radius * 2.2f,
            center = center
        )

        // LED on — white circle with brightness-based alpha
        drawCircle(
            color = LedOn.copy(alpha = alpha),
            radius = radius,
            center = center
        )
    }
}
