package com.deepu.glyphtext.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.ui.theme.AccentAmber
import com.deepu.glyphtext.ui.theme.AccentGreen
import com.deepu.glyphtext.ui.theme.LedOn
import com.deepu.glyphtext.ui.theme.NothingBlack
import com.deepu.glyphtext.ui.theme.NothingRed
import com.deepu.glyphtext.ui.theme.NothingSurface
import com.deepu.glyphtext.ui.theme.NothingSurfaceVariant
import com.deepu.glyphtext.ui.theme.TextHint
import com.deepu.glyphtext.ui.theme.TextPrimary
import com.deepu.glyphtext.ui.theme.TextSecondary
import com.deepu.glyphtext.viewmodel.MainViewModel

/**
 * MainScreen — Full UI screen for the Glyph Text Animator.
 *
 * Layout (top to bottom):
 *   1. App title + status badge
 *   2. 13×13 Glyph Simulator grid
 *   3. Frame counter (during playback)
 *   4. Text input field
 *   5. Animation type selector
 *   6. Speed slider
 *   7. Play/Stop buttons
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val inputText by viewModel.inputText.collectAsState()
    val animationSpeed by viewModel.animationSpeed.collectAsState()
    val animationType by viewModel.animationType.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentFrame by viewModel.currentFrame.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isGlyphConnected by viewModel.isGlyphConnected.collectAsState()
    val frameCount by viewModel.frameCount.collectAsState()
    val currentFrameIndex by viewModel.currentFrameIndex.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NothingBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ──
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "GLYPH TEXT",
            style = MaterialTheme.typography.headlineLarge.copy(
                letterSpacing = 6.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = TextPrimary
        )
        Text(
            text = "ANIMATOR",
            style = MaterialTheme.typography.headlineMedium.copy(
                letterSpacing = 10.sp,
                fontWeight = FontWeight.Light
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Connection Status Badge ──
        StatusBadge(
            isConnected = isGlyphConnected,
            statusMessage = statusMessage
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Glyph Matrix Simulator ──
        GlyphSimulatorView(
            frame = currentFrame,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Frame Counter (visible during playback) ──
        AnimatedVisibility(
            visible = isPlaying && frameCount > 0,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Text(
                text = "Frame ${currentFrameIndex + 1} / $frameCount",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Text Input ──
        OutlinedTextField(
            value = inputText,
            onValueChange = { viewModel.updateText(it) },
            label = { Text("Enter text to animate") },
            placeholder = { Text("e.g., HELLO WORLD") },
            singleLine = true,
            enabled = !isPlaying,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NothingRed,
                unfocusedBorderColor = NothingSurfaceVariant,
                disabledBorderColor = NothingSurfaceVariant.copy(alpha = 0.5f),
                focusedLabelColor = NothingRed,
                unfocusedLabelColor = TextSecondary,
                cursorColor = NothingRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextSecondary,
                focusedPlaceholderColor = TextHint,
                unfocusedPlaceholderColor = TextHint
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Animation Type Selector ──
        Text(
            text = "ANIMATION TYPE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp
            ),
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        AnimationTypeSelector(
            selectedType = animationType,
            onTypeSelected = { viewModel.updateAnimationType(it) },
            enabled = !isPlaying
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Speed Slider ──
        Text(
            text = "SPEED",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp
            ),
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fast",
                style = MaterialTheme.typography.bodyMedium,
                color = TextHint,
                modifier = Modifier.width(36.dp)
            )
            Slider(
                value = animationSpeed.toFloat(),
                onValueChange = { viewModel.updateSpeed(it.toLong()) },
                valueRange = 30f..500f,
                enabled = !isPlaying,
                colors = SliderDefaults.colors(
                    thumbColor = NothingRed,
                    activeTrackColor = NothingRed,
                    inactiveTrackColor = NothingSurfaceVariant,
                    disabledThumbColor = NothingSurfaceVariant,
                    disabledActiveTrackColor = NothingSurfaceVariant.copy(alpha = 0.5f),
                    disabledInactiveTrackColor = NothingSurfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Slow",
                style = MaterialTheme.typography.bodyMedium,
                color = TextHint,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = "${animationSpeed}ms per frame",
            style = MaterialTheme.typography.labelSmall,
            color = TextHint,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Play / Stop Buttons ──
        if (isPlaying) {
            OutlinedButton(
                onClick = { viewModel.stopAnimation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    width = 2.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(NothingRed)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NothingRed
                )
            ) {
                Text(
                    text = "■  STOP",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.playAnimation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NothingRed,
                    contentColor = LedOn
                ),
                enabled = inputText.isNotBlank()
            ) {
                Text(
                    text = "▶  PLAY ANIMATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Sub-Components ──

/**
 * Connection status badge showing Glyph state and current action.
 */
@Composable
private fun StatusBadge(
    isConnected: Boolean,
    statusMessage: String
) {
    val dotColor by animateColorAsState(
        targetValue = if (isConnected) AccentGreen else AccentAmber,
        animationSpec = tween(300),
        label = "statusDotColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isConnected) "Glyph Connected" else "Simulator Only",
            style = MaterialTheme.typography.labelSmall,
            color = dotColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "·",
            style = MaterialTheme.typography.labelSmall,
            color = TextHint
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.labelSmall,
            color = TextHint
        )
    }
}

/**
 * Toggle selector for animation type.
 */
@Composable
private fun AnimationTypeSelector(
    selectedType: FrameGenerator.AnimationType,
    onTypeSelected: (FrameGenerator.AnimationType) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NothingSurface),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FrameGenerator.AnimationType.entries.forEach { type ->
            val isSelected = type == selectedType
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) NothingRed else NothingSurface,
                animationSpec = tween(200),
                label = "typeBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) LedOn else TextSecondary,
                animationSpec = tween(200),
                label = "typeText"
            )

            val label = when (type) {
                FrameGenerator.AnimationType.SCROLL_LEFT -> "← Scroll Left"
                FrameGenerator.AnimationType.TYPING -> "⌨ Typing"
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = enabled) { onTypeSelected(type) }
                    .background(bgColor, RoundedCornerShape(12.dp))
                    .then(
                        if (isSelected) Modifier.border(
                            1.dp,
                            NothingRed.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        else Modifier
                    )
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = textColor
                )
            }
        }
    }
}
