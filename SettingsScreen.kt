package com.tictactoemaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.data.Settings
import com.tictactoemaster.data.StartingPlayer
import com.tictactoemaster.ui.components.AppCard
import com.tictactoemaster.ui.components.ConfirmDialog
import com.tictactoemaster.ui.components.OptionRow
import com.tictactoemaster.ui.components.OutlineButton
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.components.ScreenTopBar
import com.tictactoemaster.ui.components.SwitchRow
import com.tictactoemaster.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    settings: Settings,
    onSoundChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onStartingPlayerChange: (StartingPlayer) -> Unit,
    onTutorial: () -> Unit,
    onResetStats: () -> Unit,
    onBack: () -> Unit
) {
    val c = AppTheme.colors
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenTopBar("Settings", onBack, Modifier.padding(horizontal = 8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier.widthIn(max = 520.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppCard(Modifier.fillMaxWidth()) {
                        Heading("Sound & feedback")
                        SwitchRow(
                            title = "Sound effects",
                            subtitle = "Silent when your phone is on silent or vibrate",
                            checked = settings.soundEnabled,
                            onCheckedChange = onSoundChange
                        )
                        SwitchRow(
                            title = "Vibration",
                            subtitle = "A short buzz when you play and when a game ends",
                            checked = settings.vibrationEnabled,
                            onCheckedChange = onVibrationChange
                        )
                    }

                    AppCard(Modifier.fillMaxWidth()) {
                        Heading("Theme")
                        OptionRow("Dark", selected = settings.darkTheme, onSelect = { onDarkThemeChange(true) })
                        OptionRow("Light", selected = !settings.darkTheme, onSelect = { onDarkThemeChange(false) })
                    }

                    AppCard(Modifier.fillMaxWidth()) {
                        Heading("Starting player")
                        for (option in StartingPlayer.values()) {
                            OptionRow(
                                label = option.label,
                                selected = settings.startingPlayer == option,
                                onSelect = { onStartingPlayerChange(option) }
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Against the computer you always play X, so choosing Player O lets the computer open.",
                            color = c.textSecondary,
                            fontSize = 13.sp
                        )
                    }

                    OutlineButton("How to Play", onTutorial, Modifier.fillMaxWidth())
                    OutlineButton(
                        text = "Reset Statistics",
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    if (showResetDialog) {
        ConfirmDialog(
            title = "Reset statistics?",
            message = "All wins, losses, draws and streaks will be erased. This cannot be undone.",
            confirmText = "Reset",
            onConfirm = {
                showResetDialog = false
                onResetStats()
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
private fun Heading(text: String) {
    val c = AppTheme.colors
    Text(
        text = text,
        color = c.textPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(bottom = 6.dp)
            .semantics { heading() }
    )
}
