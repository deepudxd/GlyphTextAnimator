package com.deepu.glyphtext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deepu.glyphtext.ui.MainScreen
import com.deepu.glyphtext.ui.theme.GlyphTextAnimatorTheme
import com.deepu.glyphtext.viewmodel.MainViewModel

/**
 * MainActivity — Entry point for the Glyph Text Animator app.
 *
 * Sets up edge-to-edge display with the Nothing-inspired dark theme,
 * and hosts the MainScreen composable with its ViewModel.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlyphTextAnimatorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val viewModel: MainViewModel = viewModel()
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
