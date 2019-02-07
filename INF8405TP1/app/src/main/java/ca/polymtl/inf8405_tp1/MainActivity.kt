package ca.polymtl.inf8405_tp1

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ca.polymtl.inf8405_tp1.domain.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    var movesByPuzzle: MutableMap<Int, List<MoveAction>> = mutableMapOf()

    lateinit var highScoreManager: HighScoreManager

    val puzzles = listOf(
        Puzzle(
            1,
            pieces = listOf(
                Piece(1, Position(0, 2), 2, true, Horizontal()),
                Piece(2, Position(0, 0), 3, false, Horizontal()),
                Piece(3, Position(2, 1), 3, false, Vertical()),
                Piece(4, Position(5, 0), 3, false, Vertical()),
                Piece(5, Position(0, 3), 2, false, Vertical()),
                Piece(6, Position(4, 3), 2, false, Horizontal()),
                Piece(7, Position(4, 4), 2, false, Vertical()),
                Piece(8, Position(0, 5), 3, false, Horizontal())
            ),
            exitPosition = Position(5, 2),
            actions = emptyList(),
            minMoves = 15
        ),
        Puzzle(
            2,
            pieces = listOf(
                Piece(1, Position(0, 2), 2, true, Horizontal()),
                Piece(2, Position(2, 1), 2, false, Vertical()),
                Piece(3, Position(3, 1), 3, false, Vertical()),
                Piece(4, Position(4, 1), 3, false, Vertical()),
                Piece(5, Position(0, 3), 2, false, Horizontal()),
                Piece(6, Position(2, 3), 2, false, Vertical()),
                Piece(7, Position(1, 4), 2, false, Vertical()),
                Piece(8, Position(2, 5), 2, false, Horizontal())
            ),
            exitPosition = Position(5, 2),
            actions = emptyList(),
            minMoves = 17
        ),
        Puzzle(
            3,
            pieces = listOf(
                Piece(1, Position(0, 2), 2, true, Horizontal()),
                Piece(2, Position(0, 0), 2, false, Vertical()),
                Piece(3, Position(1, 0), 2, false, Horizontal()),
                Piece(4, Position(3, 0), 2, false, Horizontal()),
                Piece(5, Position(2, 1), 2, false, Vertical()),
                Piece(6, Position(3, 2), 3, false, Vertical()),
                Piece(7, Position(4, 2), 3, false, Vertical()),
                Piece(8, Position(0, 4), 3, false, Horizontal())
            ),
            exitPosition = Position(5, 2),
            actions = emptyList(),
            minMoves = 15
        )
    )

    var currentPuzzleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        highScoreManager = HighScoreManager(getSharedPreferences("highScores", Context.MODE_PRIVATE))

        loadPuzzle(puzzles[currentPuzzleId])
    }

    fun nextPuzzle() {
        currentPuzzleId = currentPuzzleId + 1
        loadPuzzle(puzzles[currentPuzzleId])
    }

    fun previousPuzzle() {
        currentPuzzleId = currentPuzzleId - 1
        loadPuzzle(puzzles[currentPuzzleId])
    }

    fun loadPuzzle(puzzle: Puzzle) {
        val hasNext = currentPuzzleId < puzzles.size - 1
        val hasPrevious = currentPuzzleId > 0

        val moves = movesByPuzzle[puzzle.id]?.toMutableList() ?: mutableListOf()

        puzzleView.onMovesChangeListener = {
            movesByPuzzle[puzzle.id] = it ?: emptyList()
            runOnUiThread {
                val size = it?.size ?: 0
                updateMovesLabel(size)
                cancelMoveButton.isEnabled = size > 0
            }
        }

        puzzleView.puzzle = puzzle
        puzzleView.currentMoves = moves

        puzzleView.onFinishedListener = {
            showSuccessAlert {
                if (hasNext) {
                    nextPuzzle()
                }
            }

            updateHighScore(puzzle)
        }

        runOnUiThread {
            updateRecordLabel(puzzle)
            updatePuzzleIdLabel(puzzle)

            nextButton.visibility = if (!hasNext) View.INVISIBLE else View.VISIBLE
            previousButton.visibility = if (!hasPrevious) View.INVISIBLE else View.VISIBLE
        }


        cancelMoveButton.setOnClickListener {
            puzzleView.cancelMove()
        }

        nextButton.setOnClickListener {
            nextPuzzle()
        }

        previousButton.setOnClickListener {
            previousPuzzle()
        }

        resetPuzzleButton.setOnClickListener {
            resetPuzzle()
        }

        pauseButton.setOnClickListener {
            finish()
        }
    }

    fun updateMovesLabel(movesCount: Int) = runOnUiThread {
        movesCountLabel.text = "Moves: ${movesCount}"
    }

    fun updateRecordLabel(puzzle: Puzzle) = runOnUiThread {
        var record = highScoreManager.getHighScore(puzzle.id).toString()
        if (record == "0") {
            record = "--"
        }
        highScoreLabel.text = "Record: ${record}/${puzzleView.puzzle?.minMoves}"
    }

    fun updatePuzzleIdLabel(puzzle: Puzzle) = runOnUiThread {
        puzzleIdLabel.text = "${puzzleView.puzzle?.id ?: ""}"
    }

    fun resetPuzzle() {
        val puzzle = puzzles[currentPuzzleId]
        puzzle.pieces.forEach {
            it.currentPosition = it.initialPosition
        }
        movesByPuzzle[puzzle.id] = emptyList()
        loadPuzzle(puzzle)
    }

    fun showSuccessAlert(postDismiss: () -> Unit = {}) {
        val alert = alert("You solved the puzzle!").build()
        alert.window.attributes.windowAnimations = R.style.DialogAnimation
        Timer().schedule(DISMISS_TIMEOUT) {
            alert.dismiss()
            postDismiss()
        }
        alert.show()
    }

    fun updateHighScore(puzzle: Puzzle) {
        movesByPuzzle[puzzle.id]?.also { moves ->
            val currentHighScore = highScoreManager.getHighScore(puzzle.id)
            if (moves.size < currentHighScore) {
                highScoreManager.writeHighScore(puzzle.id, moves.size)
            }
        }
    }

    companion object {
        val DISMISS_TIMEOUT = 3.seconds()
    }
}


