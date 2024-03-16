package io.github.ivan8m8.courierhelper.ui.utils

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.ColorRes
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import io.github.ivan8m8.courierhelper.R

// https://www.youtube.com/watch?v=wcdqoTubPrU
// https://www.youtube.com/watch?v=zEFmWTer3yo

fun Fragment.showKeyboard() {
    WindowCompat.getInsetsController(requireActivity().window, requireView())
        .show(WindowInsetsCompat.Type.ime())
}

fun Fragment.hideKeyboard() {
    WindowCompat.getInsetsController(requireActivity().window, requireView())
        .hide(WindowInsetsCompat.Type.ime())
}

fun View.addSystemBottomPadding(isConsumed: Boolean = false) {
    doOnApplyWindowInsetsWithPaddings { v, windowInsets, paddings ->
        val type = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        val insetsRect = windowInsets.getInsets(type)
        val newBottomPadding = insetsRect.bottom + paddings.bottom
        v.updatePadding(bottom = newBottomPadding)
        if (isConsumed) {
            val newInsetsRect = with(insetsRect) {
                Insets.of(left, top, right, 0)
            }
            WindowInsetsCompat.Builder()
                .setInsets(type, newInsetsRect)
                .build()
        } else {
            windowInsets
        }
    }
}

fun View.addSystemTopMargin(isConsumed: Boolean = false) {
    doOnApplyWindowInsetsWithMargins { v, windowInsets, margins ->
        val statusBarsType = WindowInsetsCompat.Type.statusBars()
        val insetsRect = windowInsets.getInsets(statusBarsType)
        val newTopMargin = insetsRect.top + margins.top
        v.updateLayoutParams<MarginLayoutParams> { updateMargins(top = newTopMargin) }
        if (isConsumed) {
            val newInsetsRect = Insets.of(insetsRect.left, 0, insetsRect.right, insetsRect.bottom)
            WindowInsetsCompat.Builder()
                .setInsets(statusBarsType, newInsetsRect)
                .build()
        } else {
            windowInsets
        }
    }
}

fun View.addSystemBottomMargin(isConsumed: Boolean = false) {
    doOnApplyWindowInsetsWithMargins { v, windowInsets, margins ->
        val type = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        val insetsRect = windowInsets.getInsets(type)
        val newBottomMargin = insetsRect.bottom + margins.bottom
        v.updateLayoutParams<MarginLayoutParams> { updateMargins(bottom = newBottomMargin) }
        if (isConsumed) {
            val newInsetsRect = with(insetsRect) {
                Insets.of(left, top, right, 0)
            }
            WindowInsetsCompat.Builder()
                .setInsets(type, newInsetsRect)
                .build()
        } else {
            windowInsets
        }
    }
}

fun View.doOnApplyWindowInsetsWithPaddings(
    block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat
) {
    val paddings = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, paddings)
    }
    requestApplyInsetsWhenAttached()
}

fun View.doOnApplyWindowInsetsWithMargins(
    block: (View, WindowInsetsCompat, Rect) -> WindowInsetsCompat
) {
    val margins = Rect(marginLeft, marginTop, marginRight, marginBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, margins)
    }
    requestApplyInsetsWhenAttached()
}

/**
 * We should not hope that Android will send us the latest insets.
 * For example, if we create a `View` programmatically, or if we
 * do not set an insets listener right away, we have to ask Android
 * for insets on our own.
 */
private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }
            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun Fragment.setTransparentStatusBar() {
    requireActivity().window.statusBarColor = Color.TRANSPARENT
}

fun Fragment.setColoredStatusBar(colorString: String) {
    requireActivity().window.statusBarColor = Color.parseColor(colorString)
}

fun Fragment.setColoredStatusBar(@ColorRes colorRes: Int = R.color.colorPrimary) {
    requireActivity().window.statusBarColor = requireContext().getColor(colorRes)
}