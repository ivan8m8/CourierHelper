package io.github.ivan8m8.courierhelper.core.common.ui

import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.data.utils.lazyUnsafe
import io.github.ivan8m8.courierhelper.ui.utils.doOnApplyWindowInsetsWithPaddings

/**
 * Base class for a modal bottom sheet that always slides below the status bar and
 * never adds any unexpected bottom padding, allowing its content to be properly
 * drawn and scrolled under the navigation bar.
 *
 * For some reason, the [BottomSheetBehavior] in a regular [BottomSheetDialog]
 * handles the bottom inset by adding it as a bottom padding to the sheet's
 * container. So as soon as the sheet's top starts reaching the status bar,
 * that unexpected bottom padding starts being visible. And this is
 * [considered intended behavior](https://github.com/material-components/material-components-android/issues/2596).
 */
open class BaseModalBottomSheetFragment(
    @LayoutRes contentLayoutId: Int
) : BottomSheetDialogFragment(contentLayoutId) {

    /**
     * If you want to add extra top offset in addition to the status bar height.
     */
    @Px
    protected open val extraTopOffset = 0

    /**
     * If you want the top status bar offset to be multiplied by this number.
     */
    @Px
    protected open val topInsetMultiplier = 1

    protected val bottomSheetBehavior by lazyUnsafe { (requireDialog() as BottomSheetDialog).behavior }

    override fun getTheme() = R.style.ThemeOverlay_BaseModalBottomSheetDialog

    override fun onStart() {
        super.onStart()
        requireView().doOnApplyWindowInsetsWithPaddings { _, windowInsets, paddings ->
            val type = WindowInsetsCompat.Type.systemBars()
            val insetsRect = windowInsets.getInsets(type)
            val expandedOffset = insetsRect.top * topInsetMultiplier + extraTopOffset + paddings.top
            bottomSheetBehavior.maxHeight = resources.displayMetrics.heightPixels - expandedOffset
            windowInsets
        }
    }
}