package com.tictactoemaster.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.ui.components.AppCard
import com.tictactoemaster.ui.components.AppLogo
import com.tictactoemaster.ui.components.GradientButton
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.theme.AppTheme

private val steps = listOf(
    "Two players take turns. Player 1 is X and Player 2 (or the computer) is O. Tap an empty square to place your mark.",
    "Get three of your marks in a row, across, down or diagonally, to win the game.",
    "If all nine squares are filled and nobody has a line of three, the game is a draw.",
    "Playing the computer? Pick Easy, Medium or Hard from the menu. Hard never loses, so a draw is a great result!"
)

@Composable
fun TutorialScreen(onDone: () -> Unit) {
    val c = AppTheme.colors
    ScreenBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                Modifier.widthIn(max = 520.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo(Modifier.size(96.dp))
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "How to Play",
                    modifier = Modifier.semantics { heading() },
                    color = c.textPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                AppCard(Modifier.fillMaxWidth()) {
                    steps.forEachIndexed { index, text ->
                        if (index > 0) Spacer(Modifier.height(14.dp))
                        StepRow(number = index + 1, text = text)
                    }
                }
                Spacer(Modifier.height(20.dp))
                GradientButton("Got it!", onDone, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    val c = AppTheme.colors
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(32.dp)
                .background(Brush.horizontalGradient(listOf(c.accentStart, c.accentEnd)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.size(14.dp))
        Text(text, color = c.textPrimary, fontSize = 16.sp, lineHeight = 22.sp)
    }
}
