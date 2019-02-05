package ca.polymtl.inf8405_tp1.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ca.polymtl.inf8405_tp1.domain.*

class PuzzleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var puzzle: Puzzle? = null

    var draggingPiece: Piece? = null
    var draggingStartPosition: Position? = null
    var actionsToApply: MutableList<MoveAction> = mutableListOf()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val position = Position(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                draggingPiece = puzzle?.pieces?.find { it.getRekt(TILE_SIZE).contains(position.x, position.y) }
                draggingStartPosition = draggingPiece?.currentPosition?.copy()
            }

            MotionEvent.ACTION_MOVE -> {
                draggingPiece?.also { draggingPiece ->
                    draggingStartPosition?.also { draggingStartPosition ->
                        val offset = TILE_SIZE / 2
                        var newPosition: Position? = null
                        when (draggingPiece.orientation) {
                            is Horizontal -> {
                                val draggingPieceMiddleX = draggingPiece.currentPosition.x * TILE_SIZE + draggingPiece.getRekt(TILE_SIZE).width() / 2
                                if (position.x > draggingPieceMiddleX + offset) {
                                    newPosition = draggingPiece.currentPosition.copy(x = draggingPiece.currentPosition.x + 1)
                                } else if (position.x < draggingPieceMiddleX - offset) {
                                    newPosition = draggingPiece.currentPosition.copy(x = draggingPiece.currentPosition.x - 1)
                                }
                            }
                            is Vertical -> {
                                val draggingPieceMiddleY = draggingPiece.currentPosition.y * TILE_SIZE + draggingPiece.getRekt(TILE_SIZE).height() / 2
                                if (position.y > draggingPieceMiddleY + offset) {
                                    newPosition = draggingPiece.currentPosition.copy(y = draggingPiece.currentPosition.y + 1)
                                } else if (position.y < draggingPieceMiddleY - offset) {
                                    newPosition = draggingPiece.currentPosition.copy(y = draggingPiece.currentPosition.y - 1)
                                }
                            }
                        }
                        if (newPosition != null) {
                            val oldPos = draggingPiece.currentPosition.copy()
                            draggingPiece.currentPosition = newPosition
                            if (intersectsOtherPieces(draggingPiece) || !isPieceInBound(draggingPiece)) {
                                draggingPiece.currentPosition = oldPos
                            } else invalidate()
                        }
                    }
                }

            }

            MotionEvent.ACTION_UP -> {
                draggingPiece?.also { draggingPiece ->
                    draggingStartPosition?.also { draggingStartPosition ->
                        val delta = draggingPiece.currentPosition - draggingStartPosition
                        if (delta.length() > 0) {
                            actionsToApply.add(MoveAction(delta, draggingPiece.id))
                        }
                    }
                }
                draggingPiece = null
                draggingStartPosition = null
                actionsToApply = mutableListOf()
            }
        }

        return true
    }

    private fun isPieceInBound(piece: Piece) =
        piece.currentPosition.x >= 0 && piece.currentPosition.x + piece.length - 1 < GRID_SIZE &&
            piece.currentPosition.y >= 0 && piece.currentPosition.y + piece.length - 1 < GRID_SIZE

    private fun intersectsOtherPieces(piece: Piece) = puzzle?.pieces?.filterNot { it.id == piece.id }?.any { other ->
        return piece.getRekt(TILE_SIZE).intersect(other.getRekt(TILE_SIZE))
    } ?: false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.also { canvas ->
            puzzle?.also { puzzle ->
                puzzle.pieces.forEach {
                    canvas.drawRect(it.getRekt(TILE_SIZE), Paint().apply {
                        style = Paint.Style.FILL
                        color = if (it.isPlayer) Color.RED else Color.BLUE
                    })
                }
            }

            canvas.drawRect(Rect(0, 0, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE), Paint().apply {
                style = Paint.Style.STROKE
                color = Color.BLACK
                strokeWidth = 5f
            })
        }
    }

    companion object {
        val TILE_SIZE = 150
        val GRID_SIZE = 6
    }
}
