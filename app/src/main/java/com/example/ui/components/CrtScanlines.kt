package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.NeonGreen

/**
 * Renders CRT scanline raster and animated phosphor beam.
 */
@Composable
fun CrtScanlineOverlay(
    modifier: Modifier = Modifier,
    scanlineAlpha: Float = 0.08f,
    lineSpacing: Float = 6f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crt")
    val beamOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beam"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw horizontal raster scanlines
        var y = 0f
        while (y < height) {
            drawLine(
                color = Color.Black.copy(alpha = scanlineAlpha),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.5f
            )
            y += lineSpacing
        }

        // Draw moving subtle phosphor beam
        val beamY = beamOffset * height
        drawLine(
            color = NeonGreen.copy(alpha = 0.03f),
            start = Offset(0f, beamY),
            end = Offset(width, beamY),
            strokeWidth = 16f
        )
    }
}

/**
 * Fullscreen blackout glitch overlay when city grid power is cut.
 */
@Composable
fun BlackoutOverlay(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "blackout")
    val flashAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack.copy(alpha = flashAlpha))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Red warning grid lines
            for (i in 0..10) {
                val y = (height / 10f) * i
                drawLine(
                    color = AlertRed.copy(alpha = 0.4f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f
                )
            }
        }
    }
}
