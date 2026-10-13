package com.tictactoemaster.game

/** A player's symbol. */
enum class Mark {
    X, O;

    val other: Mark get() = if (this == X) O else X
}

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class GameMode { TWO_PLAYER, VS_COMPUTER }

/** Result of a finished game. */
sealed interface Outcome {
    data class Win(val winner: Mark, val line: List<Int>) : Outcome
    data object Draw : Outcome
}

/** Pure rule helpers: no Android dependencies, easy to unit test. */
object Rules {
    /** The eight winning combinations (cell indexes 0..8, row by row). */
    val WINNING_LINES: List<List<Int>> = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // columns
        listOf(0, 4, 8), listOf(2, 4, 6)                   // diagonals
    )

    /** Returns the win (with its three cells) if any player completed a line. */
    fun findWin(cells: List<Mark?>): Outcome.Win? {
        for (line in WINNING_LINES) {
            val mark = cells[line[0]]
            if (mark != null && mark == cells[line[1]] && mark == cells[line[2]]) {
                return Outcome.Win(mark, line)
            }
        }
        return null
    }

    /** Win, draw (board full, no winner) or null when the game is still running. */
    fun outcomeOf(cells: List<Mark?>): Outcome? {
        val win = findWin(cells)
        if (win != null) return win
        if (cells.all { it != null }) return Outcome.Draw
        return null
    }
}

/** Immutable 3x3 board, cells indexed 0..8 row by row. */
data class Board(val cells: List<Mark?> = List(CELL_COUNT) { null }) {
    init {
        require(cells.size == CELL_COUNT) { "A board has exactly $CELL_COUNT cells" }
    }

    val emptyCells: List<Int> get() = cells.indices.filter { cells[it] == null }

    fun isEmpty(index: Int): Boolean = index in cells.indices && cells[index] == null

    fun place(index: Int, mark: Mark): Board {
        require(isEmpty(index)) { "Cell $index is not available" }
        val copy = cells.toMutableList()
        copy[index] = mark
        return Board(copy)
    }

    companion object {
        const val CELL_COUNT = 9
    }
}

/** Complete state of one round. All moves go through [play], which rejects invalid ones. */
data class GameState(
    val board: Board = Board(),
    val current: Mark = Mark.X,
    val outcome: Outcome? = null
) {
    val isOver: Boolean get() = outcome != null
    val hasStarted: Boolean get() = board.cells.any { it != null }

    /** Returns the next state, or null when the move is illegal (occupied cell, bad index, game over). */
    fun play(index: Int): GameState? {
        if (isOver || !board.isEmpty(index)) return null
        val newBoard = board.place(index, current)
        return GameState(
            board = newBoard,
            current = current.other,
            outcome = Rules.outcomeOf(newBoard.cells)
        )
    }
}
