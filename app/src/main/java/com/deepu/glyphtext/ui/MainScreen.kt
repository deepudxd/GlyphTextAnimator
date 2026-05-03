package com.deepu.glyphtext.ui

import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepu.glyphtext.R
import com.deepu.glyphtext.engine.FrameGenerator
import com.deepu.glyphtext.ui.theme.AccentAmber
import com.deepu.glyphtext.ui.theme.AccentGreen
import com.deepu.glyphtext.ui.theme.NothingBlack
import com.deepu.glyphtext.ui.theme.NothingGray
import com.deepu.glyphtext.ui.theme.NothingGrayLight
import com.deepu.glyphtext.ui.theme.NothingSurface
import com.deepu.glyphtext.ui.theme.NothingWhite
import com.deepu.glyphtext.ui.theme.TextHint
import com.deepu.glyphtext.ui.theme.TextPrimary
import com.deepu.glyphtext.ui.theme.TextSecondary
import com.deepu.glyphtext.viewmodel.MainViewModel

/**
 * MainScreen — Full UI screen for GlyphType.
 *
 * Layout (top to bottom):
 *   1. GlyphType branded header (icon + name + tagline)
 *   2. Connection status badge
 *   3. 13×13 Glyph Simulator grid (circular dots)
 *   4. Frame counter (during playback)
 *   5. Text input field (pill-shaped)
 *   6. Animation type selector
 *   7. Speed slider
 *   8. Play/Stop + Glyph Toy buttons (pill-shaped)
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
        // ── GlyphType Header ──
        Spacer(modifier = Modifier.height(24.dp))
        GlyphTypeHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // ── Connection Status Badge ──
        StatusBadge(
            isConnected = isGlyphConnected,
            statusMessage = statusMessage
        )

        Spacer(modifier = Modifier.height(6.dp))

        // ── Subtle divider ──
        HorizontalDivider(
            color = NothingGray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
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

        // ── Text Input (pill-shaped, dark background) ──
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
                focusedBorderColor = NothingWhite,
                unfocusedBorderColor = NothingGray,
                disabledBorderColor = NothingGray.copy(alpha = 0.5f),
                focusedLabelColor = NothingWhite,
                unfocusedLabelColor = TextSecondary,
                cursorColor = NothingWhite,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextSecondary,
                focusedPlaceholderColor = TextHint,
                unfocusedPlaceholderColor = TextHint,
                focusedContainerColor = NothingSurface,
                unfocusedContainerColor = NothingSurface,
                disabledContainerColor = NothingSurface.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(28.dp),
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
                    thumbColor = NothingWhite,
                    activeTrackColor = NothingWhite,
                    inactiveTrackColor = NothingGray,
                    disabledThumbColor = NothingGrayLight,
                    disabledActiveTrackColor = NothingGrayLight.copy(alpha = 0.5f),
                    disabledInactiveTrackColor = NothingGray.copy(alpha = 0.3f)
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

        // ── Play / Stop Buttons (pill-shaped with press animation) ──
        if (isPlaying) {
            PressableButton(
                onClick = { viewModel.stopAnimation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                isOutlined = true
            ) {
                Text(
                    text = "■  STOP",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NothingWhite
                )
            }
        } else {
            PressableButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.playAnimation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = inputText.isNotBlank()
            ) {
                Text(
                    text = "▶  PLAY ANIMATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NothingBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Set as Glyph Toy Button (pill-shaped, outlined) ──
        val context = LocalContext.current
        PressableButton(
            onClick = {
                viewModel.saveForGlyphToy()
                openGlyphSettings(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            isOutlined = true,
            outlineColor = NothingGrayLight,
            enabled = inputText.isNotBlank() && !isPlaying
        ) {
            Text(
                text = "⚡  SET AS GLYPH TOY",
                style = MaterialTheme.typography.titleMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Sub-Components ──

/**
 * GlyphType branded header — icon + app name + tagline.
 * Left-aligned, Nothing OS style.
 */
@Composable
private fun GlyphTypeHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon + App Name row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "GlyphType icon",
                tint = NothingWhite,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "GlyphType",
                style = MaterialTheme.typography.headlineLarge.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tagline
        Text(
            text = "Type  •  Animate  •  Glow",
            style = MaterialTheme.typography.headlineMedium.copy(
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Light
            ),
            color = TextHint
        )
    }
}

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
                .size(6.dp)
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
 * Toggle selector for animation type — pill-shaped chips, monochrome.
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
            .clip(RoundedCornerShape(28.dp))
            .background(NothingSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FrameGenerator.AnimationType.entries.forEach { type ->
            val isSelected = type == selectedType
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) NothingWhite else NothingSurface,
                animationSpec = tween(200),
                label = "typeBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) NothingBlack else TextSecondary,
                animationSpec = tween(200),
                label = "typeText"
            )

            val label = when (type) {
                FrameGenerator.AnimationType.SCROLL_LEFT -> "← Scroll"
                FrameGenerator.AnimationType.TYPING -> "⌨ Typing"
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = enabled) { onTypeSelected(type) }
                    .background(bgColor, RoundedCornerShape(24.dp))
                    .padding(vertical = 12.dp),
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

/**
 * Pill-shaped button with subtle scale-down press animation.
 */
@Composable
private fun PressableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isOutlined: Boolean = false,
    outlineColor: androidx.compose.ui.graphics.Color = NothingWhite,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "buttonScale"
    )

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.scale(scale),
            shape = RoundedCornerShape(28.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = enabled).copy(
                width = 1.dp,
                brush = SolidColor(
                    if (enabled) outlineColor else outlineColor.copy(alpha = 0.3f)
                )
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NothingWhite,
                disabledContentColor = TextHint
            ),
            enabled = enabled,
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                .also { source ->
                    // Track press state for scale animation
                }
        ) {
            content()
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    }
                },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NothingWhite,
                contentColor = NothingBlack,
                disabledContainerColor = NothingGray,
                disabledContentColor = TextHint
            ),
            enabled = enabled
        ) {
            content()
        }
    }
}

/**
 * Open the Nothing Phone Glyph settings where the user can select Glyph Toys.
 * Tries the Nothing Glyph Interface settings first, then falls back to general settings.
 * Always shows a toast guiding the user to the correct location.
 */
private fun openGlyphSettings(context: android.content.Context) {
    Toast.makeText(
        context,
        "Text saved! Go to Glyph Interface → Glyph Toy → select \"GlyphType\"",
        Toast.LENGTH_LONG
    ).show()

    // Try Nothing's Glyph Interface settings directly
    val glyphIntents = listOf(
        // Nothing Phone Glyph Interface settings
        Intent().apply {
            component = ComponentName(
                "com.nothing.smartcenter",
                "com.nothing.smartcenter.glyph.GlyphSettingsActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        // Alternative: Nothing's general settings panel
        Intent().apply {
            component = ComponentName(
                "com.nothing.smartcenter",
                "com.nothing.smartcenter.MainActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        // Fallback: open the device's general Settings
        Intent(android.provider.Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )

    for (intent in glyphIntents) {
        try {
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // Try the next intent
        }
    }
}
