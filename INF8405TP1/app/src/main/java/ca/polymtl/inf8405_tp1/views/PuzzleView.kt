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

    val tileSize = (resources.displayMetrics.widthPixels / 8)
    val offset = tileSize / 2

    var puzzle: Puzzle? = null
        set(value) {
            if (value != null) {
                field = value
                reset()
            }
        }
    var onFinishedListener: () -> Unit = {}
    var onMovesChangeListener: (List<MoveAction>?) -> Unit = {}

    var draggingPiece: Piece? = null
    var draggingStartPosition: Position? = null

    var currentMoves: MutableList<MoveAction> = mutableListOf()
        set(value) {
            print(value)
            field = value
            onMovesChangeListener(value)
        }

    fun reset() {
        draggingPiece = null
        draggingStartPosition = null
        invalidate()
    }

    fun cancelMove() {
        puzzle?.also { puzzle ->
            val lastMove = currentMoves.last()
            currentMoves.remove(lastMove)
            onMovesChangeListener(currentMoves)
            puzzle.pieces.find { it.id == lastMove.pieceId }?.cancelAction(lastMove)
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(GRID_SIZE * tileSize, GRID_SIZE * tileSize)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val position = Position(event?.x?.toInt() ?: 0, event?.y?.toInt() ?: 0)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> startDragging(position)
            MotionEvent.ACTION_MOVE -> ifDraggingPiece { draggingPiece, draggingStartPosition ->
                var newPosition: Position? = null
                when (draggingPiece.orientation) {
                    is Horizontal -> {
                        newPosition = tryMovePieceHorizontally(draggingPiece, position)
                    }
                    is Vertical -> {
                        newPosition = tryMovePieceVertically(draggingPiece, position)
                    }
                }
                if (newPosition != null) {
                    updatePiecePosition(draggingPiece, newPosition)
                }
            }

            MotionEvent.ACTION_UP -> ifDraggingPiece { draggingPiece, draggingStartPosition ->
                stopDragging(draggingPiece, draggingStartPosition)
            }
        }

        return true
    }

    private fun tryMovePieceHorizontally(piece: Piece, dragPosition: Position): Position? {
        val draggingPieceMiddleX = piece.currentPosition.x * tileSize + piece.getRect(tileSize).width() / 2
        if (dragPosition.x > draggingPieceMiddleX + offset) {
            return piece.currentPosition.copy(x = piece.currentPosition.x + 1)
        } else if (dragPosition.x < draggingPieceMiddleX - offset) {
            return piece.currentPosition.copy(x = piece.currentPosition.x - 1)
        }
        return null
    }

    private fun tryMovePieceVertically(piece: Piece, dragPosition: Position): Position? {
        val draggingPieceMiddleY = piece.currentPosition.y * tileSize + piece.getRect(tileSize).height() / 2
        if (dragPosition.y > draggingPieceMiddleY + offset) {
            return piece.currentPosition.copy(y = piece.currentPosition.y + 1)
        } else if (dragPosition.y < draggingPieceMiddleY - offset) {
            return piece.currentPosition.copy(y = piece.currentPosition.y - 1)
        }
        return null
    }

    private fun updatePiecePosition(piece: Piece, position: Position) {
        val oldPos = piece.currentPosition.copy()
        piece.currentPosition = position
        if (intersectsOtherPieces(piece) || !isInBound(piece)) {
            piece.currentPosition = oldPos
        } else invalidate()
    }

    private fun stopDragging(piece: Piece, startPosition: Position) {
        val delta = piece.currentPosition - startPosition
        if (delta.length() > 0) {
            puzzle?.also { puzzle ->
                currentMoves.add(MoveAction(delta, piece.id))
                onMovesChangeListener(currentMoves)

                if (isPuzzleFinished(piece)) {
                    onFinishedListener()
                }
            }
        }
    }

    private fun startDragging(position: Position) {
        draggingPiece = puzzle?.pieces?.find { it.getRect(tileSize).contains(position.x.toFloat(), position.y.toFloat()) }
        draggingStartPosition = draggingPiece?.currentPosition?.copy()
    }

    private fun isPuzzleFinished(piece: Piece) = piece.isPlayer && isOutside(piece)

    private fun isInBound(piece: Piece) =
        piece.currentPosition.x >= 0 && piece.currentPosition.y >= 0 &&
            when (piece.orientation) {
                is Horizontal -> if (piece.isPlayer) true else piece.currentPosition.x + piece.length - 1 < GRID_SIZE
                is Vertical -> piece.currentPosition.y + piece.length - 1 < GRID_SIZE
            }

    private fun isOutside(piece: Piece) = piece.currentPosition.x + piece.length > GRID_SIZE

    private fun intersectsOtherPieces(piece: Piece) = puzzle?.pieces
        ?.filter { it != piece }
        ?.any { other -> piece.getRect(tileSize).intersect(other.getRect(tileSize)) } ?: false

    private fun ifDraggingPiece(body: (draggingPiece: Piece, draggingPosition: Position) -> Unit) {
        draggingPiece?.also { draggingPiece ->
            draggingStartPosition?.also { draggingStartPosition ->
                body(draggingPiece, draggingStartPosition)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.also { canvas ->
            puzzle?.also { puzzle ->
                puzzle.pieces.forEach {
                    canvas.drawRoundRect(it.getRect(tileSize, PADDING), 20f, 20f, Paint().apply {
                        style = Paint.Style.FILL
                        color = if (it.isPlayer) Color.RED else Color.BLUE
                    })
                }

                canvas.drawRect(Rect(0, 0, GRID_SIZE * tileSize, GRID_SIZE * tileSize), Paint().apply {
                    style = Paint.Style.STROKE
                    color = Color.BLACK
                    strokeWidth = 10f
                })

                canvas.drawLine(
                    (puzzle.exitPosition.x * tileSize + tileSize).toFloat(),
                    (puzzle.exitPosition.y * tileSize).toFloat(),
                    (puzzle.exitPosition.x * tileSize + tileSize).toFloat(),
                    (puzzle.exitPosition.y * tileSize + tileSize).toFloat(),
                    Paint().apply {
                        color = Color.RED
                        strokeWidth = 20f
                    }
                )

            }

        }
    }

    companion object {
        val PADDING = 5
        val GRID_SIZE = 6
    }
}
