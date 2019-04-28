package ca.polymtl.inf8405.Utils

import android.content.Context


var Context.currentUser: String
    set(value) {
        getSharedPreferences("", Context.MODE_PRIVATE).edit()
            .putString("currentUser", value).apply()
    }
    get() = getSharedPreferences("", Context.MODE_PRIVATE)
        .getString("currentUser", "") ?: ""