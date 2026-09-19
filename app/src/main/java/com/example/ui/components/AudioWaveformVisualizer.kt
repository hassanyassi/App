package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletLight
import kotlin.math.sin

@Composable
fun AudioWaveformVisualizer(
    isListening: Boolean,
    rmsDb: Float,
    modifier: Modifier = Modifier,
    primaryColor: Color = CyanNeon,
    secondaryColor: Color = VioletLight
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val width = size.width
        val height = size.height
        val barCount = 28
        val spacing = width / barCount
        val barWidth = spacing * 0.55f

        val effectiveRms = if (isListening) (rmsDb.coerceIn(0.1f, 1f) * pulseScale) else 0.08f

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            val wave = sin((normalizedX * 4 * Math.PI + Math.toRadians(phase.toDouble()))).toFloat()
            val variableHeight = (height * 0.15f) + (height * 0.75f * effectiveRms * (0.5f + 0.5f * kotlin.math.abs(wave)))
            val barHeight = variableHeight.coerceIn(6f, height)

            val x = i * spacing + (spacing - barWidth) / 2
            val y = (height - barHeight) / 2

            val brush = Brush.verticalGradient(
                colors = listOf(primaryColor, secondaryColor),
                startY = y,
                endY = y + barHeight
            )

            drawRoundRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
