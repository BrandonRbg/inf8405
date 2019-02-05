package ca.polymtl.inf8405_tp1.domain

data class Puzzle(
    val id: Int,
    val pieces: List<Piece>,
    val exitPosition: Position,
    val actions: List<MoveAction>,
    val minMoves: Int
)
