package ca.polymtl.inf8405_tp1.domain

import android.graphics.Rect

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

    fun getRekt(tileSize: Int = 100) = Rect(
        currentPosition.x * tileSize,
        currentPosition.y * tileSize,
        when (orientation) {
            is Horizontal -> (currentPosition.x + length) * tileSize
            is Vertical -> currentPosition.x * tileSize + tileSize
        },
        when (orientation) {
            is Horizontal -> currentPosition.y * tileSize + tileSize
            is Vertical -> (currentPosition.y + length) * tileSize
        }
    )

    fun applyAction(action: MoveAction) {
        currentPosition = currentPosition + action.movement
    }

    fun cancelAction(action: MoveAction) {
        currentPosition = currentPosition - action.movement
    }
}
