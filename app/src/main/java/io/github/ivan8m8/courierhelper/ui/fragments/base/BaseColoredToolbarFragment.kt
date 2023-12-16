package io.github.ivan8m8.courierhelper.ui.fragments.base

import androidx.annotation.LayoutRes
import io.github.ivan8m8.courierhelper.ui.utils.setColoredStatusBar
import io.github.ivan8m8.courierhelper.ui.utils.setTransparentStatusBar

abstract class BaseColoredToolbarFragment : BaseTopFragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onStart() {
        super.onStart()
        setColoredStatusBar()
    }

    override fun onStop() {
        setTransparentStatusBar()
        super.onStop()
    }
}