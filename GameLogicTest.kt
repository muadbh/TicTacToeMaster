package com.tictactoemaster

import com.tictactoemaster.game.Board
import com.tictactoemaster.game.ComputerPlayer
import com.tictactoemaster.game.Difficulty
import com.tictactoemaster.game.GameState
import com.tictactoemaster.game.Mark
import com.tictactoemaster.game.Outcome
import com.tictactoemaster.game.Rules
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    private fun boardOf(vararg cells: Mark?): Board = Board(cells.toList())

    @Test
    fun allEightLinesAreDetectedForBothPlayers() {
        assertEquals(8, Rules.WINNING_LINES.size)
        for (mark in Mark.values()) {
            for (line in Rules.WINNING_LINES) {
                val cells = MutableList<Mark?>(9) { null }
                line.forEach { cells[it] = mark }
                val win = Rules.findWin(cells)
                assertNotNull("line $line for $mark", win)
                assertEquals(mark, win!!.winner)
                assertEquals(line, win.line)
            }
        }
    }

    @Test
    fun emptyAndPartialBoardsHaveNoOutcome() {
        assertNull(Rules.outcomeOf(Board().cells))
        assertNull(Rules.outcomeOf(boardOf(Mark.X, Mark.O, null, null, Mark.X, null, null, null, null).cells))
    }

    @Test
    fun fullBoardWithoutWinnerIsADraw() {
        val board = boardOf(
            Mark.X, Mark.O, Mark.X,
            Mark.X, Mark.O, Mark.O,
            Mark.O, Mark.X, Mark.X
        )
        assertEquals(Outcome.Draw, Rules.outcomeOf(board.cells))
    }

    @Test
    fun winOnTheLastSquareIsAWinNotADraw() {
        val board = boardOf(
            Mark.X, Mark.O, Mark.X,
            Mark.O, Mark.X, Mark.O,
            Mark.O, Mark.X, Mark.X
        )
        val outcome = Rules.outcomeOf(board.cells)
        assertTrue(outcome is Outcome.Win)
        assertEquals(Mark.X, (outcome as Outcome.Win).winner)
    }

    @Test
    fun occupiedAndOutOfRangeMovesAreRejected() {
        val state = GameState().play(4)!!
        assertNull(state.play(4))
        assertNull(state.play(-1))
        assertNull(state.play(9))
    }

    @Test
    fun playersAlternateAndNoMovesAfterTheGameEnds() {
        var state = GameState()
        assertEquals(Mark.X, state.current)
        state = state.play(0)!!
        assertEquals(Mark.O, state.current)
        state = state.play(3)!!.play(1)!!.play(4)!!.play(2)!! // X completes the top row
        assertTrue(state.isOver)
        assertEquals(Mark.X, (state.outcome as Outcome.Win).winner)
        assertNull(state.play(8))
    }

    @Test
    fun easyAndMediumOnlyPickEmptySquares() {
        val random = Random(42)
        repeat(300) {
            var state = GameState()
            while (!state.isOver) {
                val difficulty = if (random.nextBoolean()) Difficulty.EASY else Difficulty.MEDIUM
                val move = ComputerPlayer.chooseMove(state.board, state.current, difficulty, random)
                assertTrue("cell $move must be empty", state.board.isEmpty(move))
                state = state.play(move)!!
            }
        }
    }

    @Test
    fun hardTakesAnImmediateWin() {
        // O to move can win at index 2.
        val board = boardOf(
            Mark.X, Mark.X, null,
            Mark.O, Mark.O, null,
            Mark.X, null, null
        )
        val state = GameState(board = board, current = Mark.O)
        assertEquals(5, ComputerPlayer.chooseMove(state.board, Mark.O, Difficulty.HARD))
    }

    @Test
    fun hardBlocksAnImmediateLoss() {
        // X threatens the top row; O has no win of its own and must block at index 2.
        val board = boardOf(
            Mark.X, Mark.X, null,
            null, Mark.O, null,
            null, null, null
        )
        assertEquals(2, ComputerPlayer.chooseMove(board, Mark.O, Difficulty.HARD))
    }

    /** Explores every possible opponent move: Hard must never lose, whichever side it plays. */
    @Test
    fun hardComputerNeverLosesAsSecondPlayer() {
        assertTrue(computerNeverLoses(GameState(), computer = Mark.O))
    }

    @Test
    fun hardComputerNeverLosesAsFirstPlayer() {
        assertTrue(computerNeverLoses(GameState(), computer = Mark.X))
    }

    private fun computerNeverLoses(state: GameState, computer: Mark): Boolean {
        val outcome = state.outcome
        if (outcome != null) {
            return !(outcome is Outcome.Win && outcome.winner != computer)
        }
        return if (state.current == computer) {
            val move = ComputerPlayer.chooseMove(state.board, computer, Difficulty.HARD)
            assertTrue(state.board.isEmpty(move))
            computerNeverLoses(state.play(move)!!, computer)
        } else {
            state.board.emptyCells.all { computerNeverLoses(state.play(it)!!, computer) }
        }
    }

    @Test
    fun hardComputerCanWinAgainstARandomPlayer() {
        val random = Random(7)
        var computerWins = 0
        repeat(50) {
            var state = GameState()
            while (!state.isOver) {
                val move = if (state.current == Mark.O) {
                    ComputerPlayer.chooseMove(state.board, Mark.O, Difficulty.HARD, random)
                } else {
                    state.board.emptyCells.random(random)
                }
                state = state.play(move)!!
            }
            val outcome = state.outcome
            assertFalse(outcome is Outcome.Win && outcome.winner == Mark.X)
            if (outcome is Outcome.Win) computerWins++
        }
        assertTrue("Hard should beat a random player most of the time", computerWins > 25)
    }
}
