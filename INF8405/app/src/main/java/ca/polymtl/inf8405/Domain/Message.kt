package ca.polymtl.inf8405.Domain

data class Message(
    val text: String = "",
    val sender: String = "",
    val timestamp: Long = 0
)