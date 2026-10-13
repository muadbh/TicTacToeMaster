package com.tictactoemaster.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tictactoemaster.game.Difficulty
import com.tictactoemaster.game.GameMode
import com.tictactoemaster.game.Mark
import com.tictactoemaster.game.Outcome
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tictactoe_master")

/** Stores settings and statistics locally with Jetpack DataStore (no network, no account). */
class SettingsRepository(context: Context) {

    private val store: DataStore<Preferences> = context.applicationContext.dataStore

    private object Keys {
        val SOUND = booleanPreferencesKey("sound_enabled")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val DARK = booleanPreferencesKey("dark_theme")
        val STARTING = stringPreferencesKey("starting_player")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val TUTORIAL_SEEN = booleanPreferencesKey("tutorial_seen")

        val CPU_WINS = intPreferencesKey("cpu_wins")
        val CPU_LOSSES = intPreferencesKey("cpu_losses")
        val CPU_DRAWS = intPreferencesKey("cpu_draws")
        val CPU_STREAK = intPreferencesKey("cpu_current_streak")
        val CPU_BEST = intPreferencesKey("cpu_best_streak")
        val PVP_X_WINS = intPreferencesKey("pvp_x_wins")
        val PVP_O_WINS = intPreferencesKey("pvp_o_wins")
        val PVP_DRAWS = intPreferencesKey("pvp_draws")

        val ALL_STATS = listOf(
            CPU_WINS, CPU_LOSSES, CPU_DRAWS, CPU_STREAK, CPU_BEST, PVP_X_WINS, PVP_O_WINS, PVP_DRAWS
        )
    }

    private val preferences: Flow<Preferences> = store.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    val settings: Flow<Settings> = preferences.map { p ->
        Settings(
            soundEnabled = p[Keys.SOUND] ?: true,
            vibrationEnabled = p[Keys.VIBRATION] ?: true,
            darkTheme = p[Keys.DARK] ?: true,
            startingPlayer = runCatching { StartingPlayer.valueOf(p[Keys.STARTING] ?: "") }
                .getOrDefault(StartingPlayer.X),
            difficulty = runCatching { Difficulty.valueOf(p[Keys.DIFFICULTY] ?: "") }
                .getOrDefault(Difficulty.MEDIUM),
            tutorialSeen = p[Keys.TUTORIAL_SEEN] ?: false
        )
    }

    val stats: Flow<Stats> = preferences.map { p ->
        Stats(
            cpuWins = p[Keys.CPU_WINS] ?: 0,
            cpuLosses = p[Keys.CPU_LOSSES] ?: 0,
            cpuDraws = p[Keys.CPU_DRAWS] ?: 0,
            cpuCurrentStreak = p[Keys.CPU_STREAK] ?: 0,
            cpuBestStreak = p[Keys.CPU_BEST] ?: 0,
            pvpXWins = p[Keys.PVP_X_WINS] ?: 0,
            pvpOWins = p[Keys.PVP_O_WINS] ?: 0,
            pvpDraws = p[Keys.PVP_DRAWS] ?: 0
        )
    }

    suspend fun setSound(enabled: Boolean) {
        store.edit { it[Keys.SOUND] = enabled }
    }

    suspend fun setVibration(enabled: Boolean) {
        store.edit { it[Keys.VIBRATION] = enabled }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        store.edit { it[Keys.DARK] = dark }
    }

    suspend fun setStartingPlayer(value: StartingPlayer) {
        store.edit { it[Keys.STARTING] = value.name }
    }

    suspend fun setDifficulty(value: Difficulty) {
        store.edit { it[Keys.DIFFICULTY] = value.name }
    }

    suspend fun setTutorialSeen(seen: Boolean) {
        store.edit { it[Keys.TUTORIAL_SEEN] = seen }
    }

    suspend fun resetStats() {
        store.edit { prefs ->
            for (key in Keys.ALL_STATS) prefs.remove(key)
        }
    }

    /**
     * Records one finished game.
     * Vs computer: the human plays [humanMark]. A win extends the streak; a loss or draw ends it.
     */
    suspend fun recordGame(mode: GameMode, humanMark: Mark, outcome: Outcome) {
        store.edit { prefs ->
            when (mode) {
                GameMode.VS_COMPUTER -> recordVsComputer(prefs, humanMark, outcome)
                GameMode.TWO_PLAYER -> recordTwoPlayer(prefs, outcome)
            }
        }
    }

    private fun recordVsComputer(prefs: MutablePreferences, humanMark: Mark, outcome: Outcome) {
        val humanWon = outcome is Outcome.Win && outcome.winner == humanMark
        if (humanWon) {
            prefs[Keys.CPU_WINS] = (prefs[Keys.CPU_WINS] ?: 0) + 1
            val streak = (prefs[Keys.CPU_STREAK] ?: 0) + 1
            prefs[Keys.CPU_STREAK] = streak
            if (streak > (prefs[Keys.CPU_BEST] ?: 0)) prefs[Keys.CPU_BEST] = streak
        } else {
            if (outcome is Outcome.Win) {
                prefs[Keys.CPU_LOSSES] = (prefs[Keys.CPU_LOSSES] ?: 0) + 1
            } else {
                prefs[Keys.CPU_DRAWS] = (prefs[Keys.CPU_DRAWS] ?: 0) + 1
            }
            prefs[Keys.CPU_STREAK] = 0
        }
    }

    private fun recordTwoPlayer(prefs: MutablePreferences, outcome: Outcome) {
        when (outcome) {
            is Outcome.Win -> {
                val key = if (outcome.winner == Mark.X) Keys.PVP_X_WINS else Keys.PVP_O_WINS
                prefs[key] = (prefs[key] ?: 0) + 1
            }
            is Outcome.Draw -> prefs[Keys.PVP_DRAWS] = (prefs[Keys.PVP_DRAWS] ?: 0) + 1
        }
    }
}
