package com.tictactoemaster.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.tictactoemaster.ui.theme.AppTheme

private fun mix(a: Offset, b: Offset, t: Float) =
    Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)

/** A line with a soft glow (two wider, translucent strokes underneath the solid one). */
fun DrawScope.drawGlowLine(color: Color, start: Offset, end: Offset, width: Float) {
    drawLine(color.copy(alpha = 0.10f), start, end, width * 3.2f, StrokeCap.Round)
    drawLine(color.copy(alpha = 0.18f), start, end, width * 2.1f, StrokeCap.Round)
    drawLine(color, start, end, width, StrokeCap.Round)
}

/** Draws an X; [progress] 0..1 animates the first stroke, then the second. */
fun DrawScope.drawX(center: Offset, radius: Float, color: Color, width: Float, progress: Float = 1f) {
    val first = (progress * 2f).coerceIn(0f, 1f)
    val second = (progress * 2f - 1f).coerceIn(0f, 1f)
    val a = Offset(center.x - radius, center.y - radius)
    val b = Offset(center.x + radius, center.y + radius)
    val c = Offset(center.x + radius, center.y - radius)
    val d = Offset(center.x - radius, center.y + radius)
    if (first > 0f) drawGlowLine(color, a, mix(a, b, first), width)
    if (second > 0f) drawGlowLine(color, c, mix(c, d, second), width)
}

/** Draws an O; [progress] 0..1 sweeps the circle. */
fun DrawScope.drawO(center: Offset, radius: Float, color: Color, width: Float, progress: Float = 1f) {
    val sweep = 360f * progress.coerceIn(0f, 1f)
    if (sweep <= 0f) return
    val topLeft = Offset(center.x - radius, center.y - radius)
    val arcSize = Size(radius * 2f, radius * 2f)
    fun arc(tint: Color, strokeWidth: Float) {
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
    arc(color.copy(alpha = 0.10f), width * 3.2f)
    arc(color.copy(alpha = 0.18f), width * 2.1f)
    arc(color, width)
}

/** The app logo: a glowing tic tac toe grid with X, O, X. */
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    val c = AppTheme.colors
    val lineColor = c.textPrimary.copy(alpha = 0.9f)
    Canvas(
        modifier
            .aspectRatio(1f)
            .semantics { contentDescription = "Tic Tac Toe Master logo" }
    ) {
        val s = size.minDimension
        val pad = s * 0.1f
        val grid = s - 2f * pad
        val cell = grid / 3f
        val stroke = s * 0.035f

        for (i in 1..2) {
            val p = pad + cell * i
            drawLine(lineColor, Offset(p, pad), Offset(p, pad + grid), stroke, StrokeCap.Round)
            drawLine(lineColor, Offset(pad, p), Offset(pad + grid, p), stroke, StrokeCap.Round)
        }
        fun centerOf(index: Int) = Offset(
            pad + cell * (index % 3) + cell / 2f,
            pad + cell * (index / 3) + cell / 2f
        )
        drawX(centerOf(0), cell * 0.26f, c.xColor, stroke * 1.4f)
        drawO(centerOf(4), cell * 0.26f, c.oColor, stroke * 1.4f)
        drawX(centerOf(8), cell * 0.26f, c.xColor, stroke * 1.4f)
    }
}
