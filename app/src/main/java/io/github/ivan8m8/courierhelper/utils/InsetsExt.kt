package io.github.ivan8m8.courierhelper.utils

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import io.github.ivan8m8.courierhelper.R

// https://www.youtube.com/watch?v=wcdqoTubPrU
// https://www.youtube.com/watch?v=zEFmWTer3yo

fun Fragment.setTransparentStatusBar() {
    requireActivity().window.statusBarColor = Color.TRANSPARENT
}

fun Fragment.setColoredStatusBar(@ColorRes colorRes: Int = R.color.colorPrimary) {
    requireActivity().window.statusBarColor = requireContext().getColor(colorRes)
}