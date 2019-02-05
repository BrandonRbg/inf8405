package ca.polymtl.inf8405_tp1.domain

import kotlin.math.sqrt

operator fun Position.plus(other: Position) = Position(x + other.x, y + other.y)
operator fun Position.minus(other: Position) = Position(x - other.x, y - other.y)
operator fun Position.div(denominator: Int) = Position(x / denominator, y / denominator)
fun Position.length() = sqrt((x * x + y * y).toDouble())

data class Position(val x: Int, val y: Int)