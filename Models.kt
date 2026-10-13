package com.tictactoemaster.data

import com.tictactoemaster.game.Difficulty
import kotlin.math.roundToInt

enum class StartingPlayer(val label: String) {
    X("Player X"),
    O("Player O"),
    ALTERNATE("Alternate each game")
}

data class Settings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkTheme: Boolean = true,
    val startingPlayer: StartingPlayer = StartingPlayer.X,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val tutorialSeen: Boolean = false
)

/**
 * Vs-computer results (from the human's point of view, the human always plays X) are tracked
 * separately from two-player results (Player 1 = X, Player 2 = O).
 */
data class Stats(
    val cpuWins: Int = 0,
    val cpuLosses: Int = 0,
    val cpuDraws: Int = 0,
    val cpuCurrentStreak: Int = 0,
    val cpuBestStreak: Int = 0,
    val pvpXWins: Int = 0,
    val pvpOWins: Int = 0,
    val pvpDraws: Int = 0
) {
    val cpuGames: Int get() = cpuWins + cpuLosses + cpuDraws
    val pvpGames: Int get() = pvpXWins + pvpOWins + pvpDraws
    val totalGames: Int get() = cpuGames + pvpGames
    val cpuWinPercent: Int
        get() = if (cpuGames == 0) 0 else (cpuWins * 100f / cpuGames).roundToInt()
}
