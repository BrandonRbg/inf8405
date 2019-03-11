package ca.polymtl.inf8405_tp2

import android.content.Context

var Context.lightTheme: Boolean
    set(value) {
        getSharedPreferences("", Context.MODE_PRIVATE).edit()
            .putBoolean("lightTheme", value).apply()
        setTheme(if (value) R.style.AppThemeLight else R.style.AppThemeDark)
    }
    get() = getSharedPreferences("", Context.MODE_PRIVATE)
        .getBoolean("lightTheme", true)

fun Context.configureTheme() {
    lightTheme = lightTheme
}