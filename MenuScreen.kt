package com.tictactoemaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.game.Difficulty
import com.tictactoemaster.ui.components.AppLogo
import com.tictactoemaster.ui.components.GradientButton
import com.tictactoemaster.ui.components.OptionRow
import com.tictactoemaster.ui.components.OutlineButton
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.theme.AppTheme
import androidx.compose.material3.AlertDialog

@Composable
fun MenuScreen(
    difficulty: Difficulty,
    onPlayFriend: () -> Unit,
    onPlayComputer: () -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onStats: () -> Unit,
    onSettings: () -> Unit,
    onTutorial: () -> Unit
) {
    val c = AppTheme.colors
    var showDifficulty by rememberSaveable { mutableStateOf(false) }

    ScreenBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppLogo(Modifier.size(132.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tic Tac Toe Master",
                modifier = Modifier.semantics { heading() },
                color = c.textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Think. Tap. Win.",
                color = c.textSecondary,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(28.dp))

            Column(
                Modifier.widthIn(max = 420.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GradientButton("Play with Friend", onPlayFriend, Modifier.fillMaxWidth())
                GradientButton("Play with Computer", onPlayComputer, Modifier.fillMaxWidth())
                OutlineButton(
                    text = "Choose Difficulty: ${difficulty.label}",
                    onClick = { showDifficulty = true },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlineButton("Game Statistics", onStats, Modifier.fillMaxWidth())
                OutlineButton("Settings", onSettings, Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onTutorial,
                colors = ButtonDefaults.textButtonColors(contentColor = c.accentText)
            ) {
                Text("How to Play", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDifficulty) {
        AlertDialog(
            onDismissRequest = { showDifficulty = false },
            containerColor = c.dialog,
            titleContentColor = c.textPrimary,
            textContentColor = c.textSecondary,
            title = { Text("Choose Difficulty", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OptionRow(
                        label = "Easy",
                        subtitle = "The computer plays random moves",
                        selected = difficulty == Difficulty.EASY,
                        onSelect = { onDifficultyChange(Difficulty.EASY); showDifficulty = false }
                    )
                    OptionRow(
                        label = "Medium",
                        subtitle = "A mix of smart and random moves",
                        selected = difficulty == Difficulty.MEDIUM,
                        onSelect = { onDifficultyChange(Difficulty.MEDIUM); showDifficulty = false }
                    )
                    OptionRow(
                        label = "Hard",
                        subtitle = "Plays perfectly (Minimax)",
                        selected = difficulty == Difficulty.HARD,
                        onSelect = { onDifficultyChange(Difficulty.HARD); showDifficulty = false }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDifficulty = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = c.accentText)
                ) { Text("Close") }
            }
        )
    }
}
