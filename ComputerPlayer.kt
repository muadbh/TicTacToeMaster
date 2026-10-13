package com.tictactoemaster.game

import kotlin.random.Random

/** Computer opponent. Always returns the index of an empty square. */
object ComputerPlayer {

    /** Chance that the Medium computer ignores strategy and just plays a random square. */
    private const val MEDIUM_RANDOM_CHANCE = 0.35

    fun chooseMove(
        board: Board,
        me: Mark,
        difficulty: Difficulty,
        random: Random = Random.Default
    ): Int {
        val empty = board.emptyCells
        require(empty.isNotEmpty()) { "No empty squares left" }
        return when (difficulty) {
            Difficulty.EASY -> empty.random(random)
            Difficulty.MEDIUM ->
                if (random.nextDouble() < MEDIUM_RANDOM_CHANCE) empty.random(random)
                else tacticalMove(board, me, random)
            Difficulty.HARD -> bestMove(board, me, random)
        }
    }

    /** Medium "smart" move: win if possible, otherwise block, otherwise random. */
    private fun tacticalMove(board: Board, me: Mark, random: Random): Int =
        completingMove(board, me)
            ?: completingMove(board, me.other)
            ?: board.emptyCells.random(random)

    private fun completingMove(board: Board, mark: Mark): Int? {
        for (i in board.emptyCells) {
            if (Rules.findWin(board.place(i, mark).cells) != null) return i
        }
        return null
    }

    /** Hard: full Minimax with alpha-beta pruning. Ties between equally good moves are broken randomly. */
    private fun bestMove(board: Board, me: Mark, random: Random): Int {
        val cells: Array<Mark?> = board.cells.toTypedArray()
        var bestScore = Int.MIN_VALUE
        val best = mutableListOf<Int>()
        for (i in board.emptyCells) {
            cells[i] = me
            val score = minimax(cells, me.other, me, 1, Int.MIN_VALUE, Int.MAX_VALUE)
            cells[i] = null
            if (score > bestScore) {
                bestScore = score
                best.clear()
                best.add(i)
            } else if (score == bestScore) {
                best.add(i)
            }
        }
        return best.random(random)
    }

    /** Score from [me]'s point of view: faster wins and slower losses score better. */
    private fun minimax(
        cells: Array<Mark?>,
        turn: Mark,
        me: Mark,
        depth: Int,
        alphaIn: Int,
        betaIn: Int
    ): Int {
        val win = Rules.findWin(cells.asList())
        if (win != null) return if (win.winner == me) 10 - depth else depth - 10
        if (cells.all { it != null }) return 0

        var alpha = alphaIn
        var beta = betaIn
        if (turn == me) {
            var best = Int.MIN_VALUE
            for (i in cells.indices) {
                if (cells[i] != null) continue
                cells[i] = turn
                val score = minimax(cells, turn.other, me, depth + 1, alpha, beta)
                cells[i] = null
                if (score > best) best = score
                if (best > alpha) alpha = best
                if (alpha >= beta) break
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in cells.indices) {
                if (cells[i] != null) continue
                cells[i] = turn
                val score = minimax(cells, turn.other, me, depth + 1, alpha, beta)
                cells[i] = null
                if (score < best) best = score
                if (best < beta) beta = best
                if (alpha >= beta) break
            }
            return best
        }
    }
}
