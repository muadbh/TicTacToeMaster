package com.tictactoemaster.ui

import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tictactoemaster.game.GameMode
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.screens.GameScreen
import com.tictactoemaster.ui.screens.MenuScreen
import com.tictactoemaster.ui.screens.SettingsScreen
import com.tictactoemaster.ui.screens.SplashScreen
import com.tictactoemaster.ui.screens.StatsScreen
import com.tictactoemaster.ui.screens.TutorialScreen
import com.tictactoemaster.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay

private const val SPLASH_MILLIS = 1600L

/** Root composable: theme, system bars, back handling and simple state-based navigation. */
@Composable
fun TicTacToeApp(vm: AppViewModel = viewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val screen by vm.screen.collectAsStateWithLifecycle()
    val game by vm.game.collectAsStateWithLifecycle()
    val stats by vm.stats.collectAsStateWithLifecycle()

    val current = settings
    // The splash screen is always dark; afterwards the saved theme applies.
    val dark = if (screen == Screen.SPLASH) true else (current?.darkTheme ?: true)

    val activity = LocalContext.current as? ComponentActivity
    DisposableEffect(dark) {
        val style = if (dark) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        }
        activity?.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
        onDispose { }
    }

    // Start the splash timer once the saved settings are loaded.
    LaunchedEffect(current != null) {
        if (current != null) {
            delay(SPLASH_MILLIS)
            vm.finishSplash()
        }
    }

    // Android back: Stats / Settings / Tutorial go to the menu. The game screen handles its own back
    // (with a confirmation). On the menu, back leaves the app as usual.
    BackHandler(enabled = screen == Screen.STATS || screen == Screen.SETTINGS || screen == Screen.TUTORIAL) {
        if (screen == Screen.TUTORIAL) vm.closeTutorial() else vm.navigate(Screen.MENU)
    }

    TicTacToeTheme(dark = dark) {
        if (current == null) {
            ScreenBackground(content = {})
        } else {
            Crossfade(targetState = screen, animationSpec = tween(250), label = "screens") { target ->
                when (target) {
                    Screen.SPLASH -> SplashScreen()
                    Screen.TUTORIAL -> TutorialScreen(onDone = { vm.closeTutorial() })
                    Screen.MENU -> MenuScreen(
                        difficulty = current.difficulty,
                        onPlayFriend = { vm.startGame(GameMode.TWO_PLAYER) },
                        onPlayComputer = { vm.startGame(GameMode.VS_COMPUTER) },
                        onDifficultyChange = { vm.setDifficulty(it) },
                        onStats = { vm.navigate(Screen.STATS) },
                        onSettings = { vm.navigate(Screen.SETTINGS) },
                        onTutorial = { vm.navigate(Screen.TUTORIAL) }
                    )
                    Screen.GAME -> GameScreen(
                        ui = game,
                        soundEnabled = current.soundEnabled,
                        onToggleSound = { vm.setSound(!current.soundEnabled) },
                        onCellClick = { vm.onCellClick(it) },
                        onRestart = { vm.rematch() },
                        onLeave = { vm.leaveGame() }
                    )
                    Screen.STATS -> StatsScreen(
                        stats = stats,
                        onBack = { vm.navigate(Screen.MENU) }
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        settings = current,
                        onSoundChange = { vm.setSound(it) },
                        onVibrationChange = { vm.setVibration(it) },
                        onDarkThemeChange = { vm.setDarkTheme(it) },
                        onStartingPlayerChange = { vm.setStartingPlayer(it) },
                        onTutorial = { vm.navigate(Screen.TUTORIAL) },
                        onResetStats = { vm.resetStats() },
                        onBack = { vm.navigate(Screen.MENU) }
                    )
                }
            }
        }
    }
}
