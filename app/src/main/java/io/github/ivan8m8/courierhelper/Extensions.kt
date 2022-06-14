package io.github.ivan8m8.courierhelper

import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

@Suppress("DEPRECATION")
private const val TRANSLUCENT_FLAGS =
    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION

fun Window.setSystemBarsColorOverTranslucent(@ColorInt color: Int) {
    clearFlags(TRANSLUCENT_FLAGS)
    this.statusBarColor = color
    this.navigationBarColor = color
}

fun Window.setTranslucentSystemBars() {
    addFlags(TRANSLUCENT_FLAGS)
}