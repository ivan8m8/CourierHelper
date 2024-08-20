package io.github.ivan8m8.courierhelper.ui.fragments.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.ivan8m8.courierhelper.data.utils.lazyUnsafe
import io.github.ivan8m8.courierhelper.ui.utils.doOnApplyWindowInsetsWithPaddings

/**
 * Base class for a modal bottom sheet that always slides below the status bar.
 *
 * As well, this never adds any unwanted bottom padding, allowing its content
 * to be properly drawn and scrolled under the status bar. Although that can
 * be achieved by setting `paddingBottomSystemWindowInsets` to `false`.
 * And there's also an [alternative way](https://stackoverflow.com/a/68937978/7541231).
 *
 * For some reason, [BottomSheetBehavior] handles the bottom inset by adding it as a
 * bottom padding to its child when the sheet's top reaches the status bar, and this is
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

    private val bottomSheetBehavior by lazyUnsafe { (requireDialog() as BottomSheetDialog).behavior }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return bottomSheetDialog.apply {
            behavior.apply {
                // used in conjunction with expandedOffset, which is set later
                isFitToContents = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Our goal here is to prevent the sheet's top from reaching the status bar
        requireView().doOnApplyWindowInsetsWithPaddings { _, windowInsets, paddings ->
            val type = WindowInsetsCompat.Type.systemBars()
            val insetsRect = windowInsets.getInsets(type)
            val expandedOffset = insetsRect.top * topInsetMultiplier + extraTopOffset + paddings.top
            bottomSheetBehavior.expandedOffset = expandedOffset
            WindowInsetsCompat.Builder()
                .setInsets(
                    type,
                    Insets.of(insetsRect.left, 0, insetsRect.right, expandedOffset)
                )
                .build()
        }
    }
}