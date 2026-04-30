package com.example.calmtask.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ConfettiPiece(var x: Float, var y: Float, val color: Color, var alpha: Float = 1f)

@Composable
fun ConfettiOverlay(trigger: Boolean, onFinish: () -> Unit = {}) {
    if (!trigger) return
    val scope = rememberCoroutineScope()
    val pieces = remember {
        List(40) {
            ConfettiPiece(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                color = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta).random()
            )
        }
    }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        offsetY.animateTo(1.5f, animationSpec = tween(1500))
        onFinish()
    }

    Canvas(Modifier.fillMaxSize()) {
        pieces.forEach { piece ->
            val drawY = piece.y + offsetY.value * size.height
            drawCircle(
                color = piece.color,
                radius = 8f,
                center = Offset(piece.x * size.width, drawY % size.height)
            )
        }
    }
}
