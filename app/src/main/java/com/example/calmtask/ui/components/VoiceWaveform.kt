package com.example.calmtask.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun VoiceWaveform(
    amplitude: Float, // 0f..1f
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val steps = 50
        val stepWidth = width / steps
        val path = Path()
        path.moveTo(0f, centerY)
        for (i in 1 until steps) {
            val x = i * stepWidth
            val y = centerY + (Math.random().toFloat() * 2 - 1) * amplitude * centerY
            path.lineTo(x, y)
        }
        drawPath(
            path,
            color = Color(0xFF4F7DF3),
            style = Stroke(width = 4f)
        )
    }
}
