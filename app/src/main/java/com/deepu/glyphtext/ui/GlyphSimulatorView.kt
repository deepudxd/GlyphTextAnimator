package com.deepu.glyphtext.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.ui.theme.LedGlow
import com.deepu.glyphtext.ui.theme.LedOff
import com.deepu.glyphtext.ui.theme.LedOn
import com.deepu.glyphtext.ui.theme.NothingBlack

/**
 * GlyphSimulatorView — Visual 13×13 LED matrix simulator.
 *
 * Renders a grid of rounded squares representing the Glyph Matrix LEDs.
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
            .padding(horizontal = 16.dp)
            .aspectRatio(1f)
            .background(
                color = NothingBlack,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val gridSize = size.width
            val cellSize = gridSize / matrixSize
            val ledSize = cellSize * 0.75f
            val ledCornerRadius = ledSize * 0.2f
            val ledOffset = (cellSize - ledSize) / 2f

            for (y in 0 until matrixSize) {
                for (x in 0 until matrixSize) {
                    val index = y * matrixSize + x
                    val brightness = if (index < frame.size) {
                        frame[index].coerceIn(0, 255)
                    } else {
                        0
                    }

                    val cellX = x * cellSize + ledOffset
                    val cellY = y * cellSize + ledOffset

                    drawLed(
                        x = cellX,
                        y = cellY,
                        size = ledSize,
                        cornerRadius = ledCornerRadius,
                        brightness = brightness
                    )
                }
            }
        }
    }
}

/**
 * Draws a single LED cell with optional glow effect.
 */
private fun DrawScope.drawLed(
    x: Float,
    y: Float,
    size: Float,
    cornerRadius: Float,
    brightness: Int
) {
    val alpha = brightness / 255f

    if (brightness == 0) {
        // LED off — dark cell
        drawRoundRect(
            color = LedOff,
            topLeft = Offset(x, y),
            size = Size(size, size),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    } else {
        // Glow layer — soft radial gradient behind the LED
        val glowPadding = size * 0.3f
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    LedGlow.copy(alpha = alpha * 0.6f),
                    Color.Transparent
                ),
                center = Offset(x + size / 2f, y + size / 2f),
                radius = size * 0.8f
            ),
            topLeft = Offset(x - glowPadding, y - glowPadding),
            size = Size(size + glowPadding * 2, size + glowPadding * 2),
            cornerRadius = CornerRadius(cornerRadius * 1.5f, cornerRadius * 1.5f)
        )

        // LED on — white with brightness-based alpha
        drawRoundRect(
            color = LedOn.copy(alpha = alpha),
            topLeft = Offset(x, y),
            size = Size(size, size),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }
}
