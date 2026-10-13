package com.tictactoemaster.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tictactoemaster.data.Settings
import com.tictactoemaster.data.SettingsRepository
import com.tictactoemaster.data.StartingPlayer
import com.tictactoemaster.data.Stats
import com.tictactoemaster.game.ComputerPlayer
import com.tictactoemaster.game.Difficulty
import com.tictactoemaster.game.GameMode
import com.tictactoemaster.game.GameState
import com.tictactoemaster.game.Mark
import com.tictactoemaster.game.Outcome
import com.tictactoemaster.platform.Haptics
import com.tictactoemaster.platform.SoundManager
import com.tictactoemaster.platform.Sfx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen { SPLASH, TUTORIAL, MENU, GAME, STATS, SETTINGS }

/** Everything the game screen needs to draw. */
data class GameUiState(
    val mode: GameMode = GameMode.TWO_PLAYER,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    /** In vs-computer games the human always plays X and the computer plays O. */
    val humanMark: Mark = Mark.X,
    val game: GameState = GameState(),
    /** Counts rematches; used for alternating the starting player and for animations. */
    val round: Int = 0,
    val computerThinking: Boolean = false
) {
    /** True while a game is in progress (at least one move made, nobody has won yet). */
    val isActive: Boolean get() = game.hasStarted && !game.isOver
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private val sound = SoundManager(application)
    private val haptics = Haptics(application)
    private var computerJob: Job? = null

    /** Null until the saved settings have been read from disk. */
    val settings: StateFlow<Settings?> =
        repository.settings.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val stats: StateFlow<Stats> =
        repository.stats.stateIn(viewModelScope, SharingStarted.Eagerly, Stats())

    private val _screen = MutableStateFlow(Screen.SPLASH)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _game = MutableStateFlow(GameUiState())
    val game: StateFlow<GameUiState> = _game.asStateFlow()

    // ---------------------------------------------------------------- navigation

    fun finishSplash() {
        if (_screen.value != Screen.SPLASH) return
        val tutorialSeen = settings.value?.tutorialSeen ?: true
        _screen.value = if (tutorialSeen) Screen.MENU else Screen.TUTORIAL
    }

    fun navigate(screen: Screen) {
        _screen.value = screen
    }

    fun closeTutorial() {
        viewModelScope.launch { repository.setTutorialSeen(true) }
        _screen.value = Screen.MENU
    }

    // ---------------------------------------------------------------- game

    fun startGame(mode: GameMode) {
        computerJob?.cancel()
        val s = settings.value ?: Settings()
        _game.value = GameUiState(
            mode = mode,
            difficulty = s.difficulty,
            game = GameState(current = starterFor(s.startingPlayer, 0)),
            round = 0
        )
        _screen.value = Screen.GAME
        maybeComputerMove()
    }

    /** Starts a fresh round with the same mode and difficulty. */
    fun rematch() {
        computerJob?.cancel()
        val s = settings.value ?: Settings()
        val round = _game.value.round + 1
        _game.update {
            it.copy(
                game = GameState(current = starterFor(s.startingPlayer, round)),
                round = round,
                computerThinking = false
            )
        }
        maybeComputerMove()
    }

    fun leaveGame() {
        computerJob?.cancel()
        _game.value = GameUiState()
        _screen.value = Screen.MENU
    }

    fun onCellClick(index: Int) {
        val ui = _game.value
        if (ui.computerThinking || ui.game.isOver) return
        if (ui.mode == GameMode.VS_COMPUTER && ui.game.current != ui.humanMark) return
        applyMove(index)
    }

    private fun starterFor(pref: StartingPlayer, round: Int): Mark = when (pref) {
        StartingPlayer.X -> Mark.X
        StartingPlayer.O -> Mark.O
        StartingPlayer.ALTERNATE -> if (round % 2 == 0) Mark.X else Mark.O
    }

    /** Applies a move if it is legal (GameState.play rejects occupied cells and finished games). */
    private fun applyMove(index: Int) {
        val ui = _game.value
        val next = ui.game.play(index) ?: return
        val mover = ui.game.current
        _game.value = ui.copy(game = next)

        val s = settings.value ?: Settings()
        val outcome = next.outcome
        if (outcome == null) {
            sound.play(if (mover == Mark.X) Sfx.PLACE_X else Sfx.PLACE_O, s.soundEnabled)
            haptics.tap(s.vibrationEnabled)
            maybeComputerMove()
            return
        }

        // The game just ended: feedback + statistics. play() refuses further moves, so this runs once.
        when (outcome) {
            is Outcome.Win -> {
                val computerWon = ui.mode == GameMode.VS_COMPUTER && outcome.winner != ui.humanMark
                sound.play(if (computerWon) Sfx.LOSE else Sfx.WIN, s.soundEnabled)
                haptics.win(s.vibrationEnabled)
            }
            is Outcome.Draw -> {
                sound.play(Sfx.DRAW, s.soundEnabled)
                haptics.draw(s.vibrationEnabled)
            }
        }
        viewModelScope.launch { repository.recordGame(ui.mode, ui.humanMark, outcome) }
    }

    private fun maybeComputerMove() {
        val ui = _game.value
        if (ui.mode != GameMode.VS_COMPUTER || ui.game.isOver || ui.game.current == ui.humanMark) return

        _game.update { it.copy(computerThinking = true) }
        computerJob?.cancel()
        computerJob = viewModelScope.launch {
            val board = ui.game.board
            val me = ui.game.current
            val move = withContext(Dispatchers.Default) {
                ComputerPlayer.chooseMove(board, me, ui.difficulty)
            }
            delay(450) // a short "thinking" pause feels more natural
            _game.update { it.copy(computerThinking = false) }
            val now = _game.value
            if (now.game.board != board || now.game.isOver) return@launch
            applyMove(move)
        }
    }

    // ---------------------------------------------------------------- settings & stats

    fun setSound(enabled: Boolean) {
        viewModelScope.launch { repository.setSound(enabled) }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { repository.setVibration(enabled) }
    }

    fun setDarkTheme(dark: Boolean) {
        viewModelScope.launch { repository.setDarkTheme(dark) }
    }

    fun setStartingPlayer(value: StartingPlayer) {
        viewModelScope.launch { repository.setStartingPlayer(value) }
    }

    fun setDifficulty(value: Difficulty) {
        viewModelScope.launch { repository.setDifficulty(value) }
    }

    fun resetStats() {
        viewModelScope.launch { repository.resetStats() }
    }

    override fun onCleared() {
        computerJob?.cancel()
        sound.release()
        super.onCleared()
    }
}
