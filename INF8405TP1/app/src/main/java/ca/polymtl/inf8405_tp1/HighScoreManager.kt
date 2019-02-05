package ca.polymtl.inf8405_tp1

import android.content.SharedPreferences

class HighScoreManager(val sharedPreferences: SharedPreferences) {

    fun getHighScore(puzzleId: Int) = sharedPreferences.getInt("$HIGH_SCORE_KEY-${puzzleId}", 0)

    fun writeHighScore(puzzleId: Int, newScore: Int) {
        sharedPreferences.edit().putInt("$HIGH_SCORE_KEY-${puzzleId}", newScore)
    }

    companion object {
        val HIGH_SCORE_KEY = "high_score"
    }
}