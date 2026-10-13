package com.tictactoemaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.data.Stats
import com.tictactoemaster.ui.components.AppCard
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.components.ScreenTopBar
import com.tictactoemaster.ui.theme.AppTheme

@Composable
fun StatsScreen(stats: Stats, onBack: () -> Unit) {
    val c = AppTheme.colors
    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenTopBar("Game Statistics", onBack, Modifier.padding(horizontal = 8.dp))
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
                        Text("Total games played", color = c.textSecondary, fontSize = 15.sp)
                        Text(
                            text = stats.totalGames.toString(),
                            color = c.textPrimary,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (stats.totalGames == 0) {
                            Text(
                                "No games yet. Play a round and your results will show up here.",
                                color = c.textSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    AppCard(Modifier.fillMaxWidth()) {
                        SectionTitle("Vs Computer")
                        StatRow(
                            StatItem("Wins", stats.cpuWins.toString(), c.xColor),
                            StatItem("Losses", stats.cpuLosses.toString(), c.oColor)
                        )
                        Spacer(Modifier.height(12.dp))
                        StatRow(
                            StatItem("Draws", stats.cpuDraws.toString()),
                            StatItem("Win rate", "${stats.cpuWinPercent}%")
                        )
                        Spacer(Modifier.height(12.dp))
                        StatRow(
                            StatItem("Current win streak", stats.cpuCurrentStreak.toString()),
                            StatItem("Best win streak", stats.cpuBestStreak.toString(), c.win)
                        )
                        Spacer(Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { stats.cpuWinPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = c.accentText,
                            trackColor = c.cardBorder
                        )
                    }

                    AppCard(Modifier.fillMaxWidth()) {
                        SectionTitle("Two Players")
                        StatRow(
                            StatItem("Player 1 (X) wins", stats.pvpXWins.toString(), c.xColor),
                            StatItem("Player 2 (O) wins", stats.pvpOWins.toString(), c.oColor)
                        )
                        Spacer(Modifier.height(12.dp))
                        StatRow(
                            StatItem("Draws", stats.pvpDraws.toString()),
                            StatItem("Games", stats.pvpGames.toString())
                        )
                    }

                    Text(
                        "A win streak counts consecutive wins against the computer. A loss or draw ends it.",
                        color = c.textSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

private class StatItem(val label: String, val value: String, val color: Color? = null)

@Composable
private fun SectionTitle(text: String) {
    val c = AppTheme.colors
    Text(
        text = text,
        color = c.textPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun StatRow(left: StatItem, right: StatItem) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile(left, Modifier.weight(1f))
        StatTile(right, Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(item: StatItem, modifier: Modifier = Modifier) {
    val c = AppTheme.colors
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(c.cell)
            .padding(14.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Text(item.label, color = c.textSecondary, fontSize = 13.sp)
        Text(
            text = item.value,
            color = item.color ?: c.textPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
