package ca.polymtl.inf8405_tp1.domain

import android.graphics.RectF

sealed class Orientation

class Horizontal : Orientation()
class Vertical : Orientation()

class Piece(
    val id: Int,
    val initialPosition: Position,
    val length: Int,
    val isPlayer: Boolean,
    val orientation: Orientation
) {

    var currentPosition = initialPosition

    fun getRect(tileSize: Int = 100, padding: Int = 0) = RectF(
        (currentPosition.x * tileSize).toFloat() + padding,
        (currentPosition.y * tileSize).toFloat() + padding,
        when (orientation) {
            is Horizontal -> ((currentPosition.x + length) * tileSize).toFloat() - padding
            is Vertical -> (currentPosition.x * tileSize + tileSize).toFloat() - padding
        },
        when (orientation) {
            is Horizontal -> (currentPosition.y * tileSize + tileSize).toFloat() - padding
            is Vertical -> ((currentPosition.y + length) * tileSize).toFloat() - padding
        }
    )

    fun cancelAction(action: MoveAction) {
        currentPosition = currentPosition - action.movement
    }
}
