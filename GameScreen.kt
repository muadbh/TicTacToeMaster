package com.tictactoemaster.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.game.GameMode
import com.tictactoemaster.game.Mark
import com.tictactoemaster.game.Outcome
import com.tictactoemaster.ui.GameUiState
import com.tictactoemaster.ui.components.ConfettiOverlay
import com.tictactoemaster.ui.components.ConfirmDialog
import com.tictactoemaster.ui.components.GameBoard
import com.tictactoemaster.ui.components.GradientButton
import com.tictactoemaster.ui.components.OutlineButton
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.components.ScreenTopBar
import com.tictactoemaster.ui.theme.AppTheme

@Composable
fun GameScreen(
    ui: GameUiState,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onCellClick: (Int) -> Unit,
    onRestart: () -> Unit,
    onLeave: () -> Unit
) {
    var showLeaveDialog by rememberSaveable { mutableStateOf(false) }

    // Leaving an active game asks for confirmation; otherwise it leaves straight away.
    val requestLeave: () -> Unit = {
        if (ui.isActive) {
            showLeaveDialog = true
        } else {
            onLeave()
        }
    }
    BackHandler(onBack = requestLeave)

    val outcome = ui.game.outcome
    val winLine = (outcome as? Outcome.Win)?.line
    val celebrate = outcome is Outcome.Win &&
        (ui.mode == GameMode.TWO_PLAYER || outcome.winner == ui.humanMark)

    ScreenBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val landscape = maxWidth > maxHeight
            if (landscape) {
                val boardSize: Dp = minOf(maxHeight - 24.dp, maxWidth * 0.55f, 520.dp).coerceAtLeast(160.dp)
                Row(
                    Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameBoard(ui.game.board, winLine, onCellClick, Modifier.size(boardSize))
                    Column(
                        Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GameHeader(ui, soundEnabled, onToggleSound, requestLeave)
                        Spacer(Modifier.height(12.dp))
                        PlayerChips(ui)
                        Spacer(Modifier.height(16.dp))
                        StatusBanner(ui)
                        Spacer(Modifier.height(16.dp))
                        GameButtons(ui, onRestart, requestLeave, Modifier.fillMaxWidth())
                    }
                }
            } else {
                val boardSize: Dp = minOf(maxWidth - 32.dp, maxHeight - 300.dp, 480.dp).coerceAtLeast(180.dp)
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GameHeader(ui, soundEnabled, onToggleSound, requestLeave)
                    Spacer(Modifier.height(8.dp))
                    PlayerChips(ui)
                    Spacer(Modifier.height(16.dp))
                    GameBoard(ui.game.board, winLine, onCellClick, Modifier.size(boardSize))
                    Spacer(Modifier.height(16.dp))
                    StatusBanner(ui)
                    Spacer(Modifier.height(16.dp))
                    GameButtons(
                        ui, onRestart, requestLeave,
                        Modifier.widthIn(max = 480.dp).fillMaxWidth()
                    )
                }
            }
            if (celebrate) ConfettiOverlay(roundKey = ui.round)
        }
    }

    if (showLeaveDialog) {
        ConfirmDialog(
            title = "Leave this game?",
            message = "Your game is still in progress. If you leave now it will not count towards your statistics.",
            confirmText = "Leave",
            dismissText = "Keep playing",
            onConfirm = {
                showLeaveDialog = false
                onLeave()
            },
            onDismiss = { showLeaveDialog = false }
        )
    }
}

@Composable
private fun GameHeader(
    ui: GameUiState,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onBack: () -> Unit
) {
    val title = if (ui.mode == GameMode.TWO_PLAYER) "Two Players" else "Vs Computer · ${ui.difficulty.label}"
    ScreenTopBar(
        title = title,
        onBack = onBack,
        trailing = {
            Text(
                text = if (soundEnabled) "🔊" else "🔇",
                fontSize = 24.sp,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(role = Role.Button, onClick = onToggleSound)
                    .semantics {
                        contentDescription = if (soundEnabled) "Turn sound off" else "Turn sound on"
                    }
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    )
}

@Composable
private fun PlayerChips(ui: GameUiState) {
    val g = ui.game
    val twoPlayer = ui.mode == GameMode.TWO_PLAYER
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PlayerChip(Mark.X, if (twoPlayer) "Player 1" else "You", !g.isOver && g.current == Mark.X)
        PlayerChip(Mark.O, if (twoPlayer) "Player 2" else "Computer", !g.isOver && g.current == Mark.O)
    }
}

@Composable
private fun PlayerChip(mark: Mark, label: String, active: Boolean) {
    val c = AppTheme.colors
    val color = if (mark == Mark.X) c.xColor else c.oColor
    val shape = RoundedCornerShape(16.dp)
    val borderColor by animateColorAsState(
        targetValue = if (active) color else c.cardBorder,
        label = "chipBorder"
    )
    Row(
        Modifier
            .clip(shape)
            .background(if (active) color.copy(alpha = 0.18f) else c.card)
            .border(2.dp, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mark.name, color = color, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.size(8.dp))
        Text(label, color = c.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatusBanner(ui: GameUiState) {
    val c = AppTheme.colors
    val outcome = ui.game.outcome
    val color = when (outcome) {
        is Outcome.Win -> if (outcome.winner == Mark.X) c.xColor else c.oColor
        else -> c.textPrimary
    }
    Text(
        text = statusText(ui),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        color = color,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

private fun statusText(ui: GameUiState): String {
    val twoPlayer = ui.mode == GameMode.TWO_PLAYER
    return when (val outcome = ui.game.outcome) {
        is Outcome.Win -> when {
            twoPlayer && outcome.winner == Mark.X -> "X wins! Player 1 takes it"
            twoPlayer -> "O wins! Player 2 takes it"
            outcome.winner == ui.humanMark -> "X wins! You beat the computer"
            else -> "O wins! The computer got you"
        }
        is Outcome.Draw -> "It's a draw!"
        null -> {
            val current = ui.game.current
            when {
                twoPlayer -> if (current == Mark.X) "Player 1's turn (X)" else "Player 2's turn (O)"
                current == ui.humanMark -> "Your turn (X)"
                ui.computerThinking -> "Computer is thinking…"
                else -> "Computer's turn (O)"
            }
        }
    }
}

@Composable
private fun GameButtons(
    ui: GameUiState,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        GradientButton(
            text = if (ui.game.isOver) "Play Again" else "Restart Game",
            onClick = onRestart,
            modifier = Modifier.weight(1f)
        )
        OutlineButton(
            text = "Home",
            onClick = onHome,
            modifier = Modifier.weight(1f)
        )
    }
}
