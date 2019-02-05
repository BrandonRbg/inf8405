package ca.polymtl.inf8405_tp1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.polymtl.inf8405_tp1.domain.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        puzzleView.puzzle = Puzzle(
            1,
            pieces = listOf(
                Piece(1, Position(2, 0), 3, false, Vertical()),
                Piece(2, Position(0, 3), 2, true, Horizontal())
            ),
            exitPosition = Position(5, 2),
            actions = emptyList(),
            minMoves = 2
        )
    }
}
