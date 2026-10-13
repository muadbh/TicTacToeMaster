package com.tictactoemaster.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tictactoemaster.game.Board
import com.tictactoemaster.game.Mark
import com.tictactoemaster.ui.theme.AppTheme

/**
 * 3x3 board. Every square is a large touch target, winning squares glow, and the other marks
 * dim once the game is won. The caller decides the size (the board keeps a 1:1 aspect ratio).
 */
@Composable
fun GameBoard(
    board: Board,
    winLine: List<Int>?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (row in 0 until 3) {
            Row(
                Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val highlighted = winLine?.contains(index) == true
                    BoardCell(
                        mark = board.cells[index],
                        index = index,
                        highlighted = highlighted,
                        dimmed = winLine != null && !highlighted,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    mark: Mark?,
    index: Int,
    highlighted: Boolean,
    dimmed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = AppTheme.colors
    val shape = RoundedCornerShape(18.dp)

    // 0 -> 1 draws the symbol. Starts at 1 for marks that already exist (e.g. after rotation).
    val progress = remember { Animatable(if (mark != null) 1f else 0f) }
    LaunchedEffect(mark) {
        if (mark == null) {
            progress.snapTo(0f)
        } else {
            progress.animateTo(1f, tween(durationMillis = 300, easing = FastOutSlowInEasing))
        }
    }

    val description = "Row ${index / 3 + 1}, column ${index % 3 + 1}, " + when (mark) {
        null -> "empty"
        Mark.X -> "X"
        Mark.O -> "O"
    }

    Box(
        modifier
            .semantics(mergeDescendants = true) { contentDescription = description }
            .clip(shape)
            .background(c.cell)
            .border(if (highlighted) 2.dp else 1.dp, if (highlighted) c.win else c.cardBorder, shape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        if (highlighted) WinGlow(color = c.win, shape = shape)
        if (mark != null) {
            val color = if (mark == Mark.X) c.xColor else c.oColor
            Canvas(
                Modifier
                    .fillMaxSize()
                    .padding(22.dp)
                    .graphicsLayer {
                        alpha = if (dimmed) 0.35f else 1f
                        val s = 0.75f + 0.25f * progress.value
                        scaleX = s
                        scaleY = s
                    }
            ) {
                val half = size.minDimension / 2f
                val width = size.minDimension * 0.12f
                val center = Offset(size.width / 2f, size.height / 2f)
                if (mark == Mark.X) {
                    drawX(center, half - width, color, width, progress.value)
                } else {
                    drawO(center, half - width, color, width, progress.value)
                }
            }
        }
    }
}

/** Pulsing highlight behind the three winning squares. */
@Composable
private fun WinGlow(color: Color, shape: Shape) {
    val transition = rememberInfiniteTransition(label = "winGlow")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    Box(Modifier.fillMaxSize().background(color.copy(alpha = glowAlpha), shape))
}
