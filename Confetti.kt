package com.tictactoemaster.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.sin
import kotlin.random.Random

private class Particle(
    val x: Float,
    val delay: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val sway: Float,
    val phase: Float,
    val spin: Float,
    val round: Boolean
)

private val ConfettiColors = listOf(
    Color(0xFF4DB5FF), Color(0xFFFF6FB5), Color(0xFFFFD166),
    Color(0xFF7CF29C), Color(0xFFB39DFF), Color(0xFFFFFFFF)
)

/** Falling confetti, played once per [roundKey]. Does not intercept touches. */
@Composable
fun ConfettiOverlay(roundKey: Int, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    val particles = remember(roundKey) {
        val random = Random(roundKey * 7919 + 13)
        List(90) {
            Particle(
                x = random.nextFloat(),
                delay = random.nextFloat() * 0.4f,
                speed = 0.8f + random.nextFloat() * 0.4f,
                size = 7f + random.nextFloat() * 8f,
                color = ConfettiColors[random.nextInt(ConfettiColors.size)],
                sway = 10f + random.nextFloat() * 30f,
                phase = random.nextFloat() * 6.28f,
                spin = 1f + random.nextFloat() * 3f,
                round = random.nextBoolean()
            )
        }
    }
    val progress = remember(roundKey) { Animatable(0f) }
    LaunchedEffect(roundKey) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 3200, easing = LinearEasing))
    }

    Canvas(modifier.fillMaxSize()) {
        val t = progress.value
        if (t <= 0f || t >= 1f) return@Canvas
        for (p in particles) {
            val local = ((t - p.delay) / (1f - p.delay)).coerceIn(0f, 1f)
            if (local <= 0f) continue
            val y = -20f + local * p.speed * (size.height + 60f)
            val x = p.x * size.width + sin(local * 6f + p.phase) * p.sway * density
            val alpha = if (local > 0.85f) (1f - local) / 0.15f else 1f
            val color = p.color.copy(alpha = alpha)
            val s = p.size * density
            if (p.round) {
                drawCircle(color, radius = s / 2f, center = Offset(x, y))
            } else {
                rotate(degrees = local * p.spin * 360f, pivot = Offset(x, y)) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x - s / 2f, y - s / 3f),
                        size = Size(s, s * 0.6f)
                    )
                }
            }
        }
    }
}
