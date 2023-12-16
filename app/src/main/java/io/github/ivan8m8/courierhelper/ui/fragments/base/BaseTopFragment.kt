package io.github.ivan8m8.courierhelper.ui.fragments.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * If a regular Fragment lies on top of another one (e.g. committed via transaction.add()),
 * the behind one receives the unconsumed touch events of the top one.
 * BaseTopFragment prevents this issue by consuming all the touch events.
 */
abstract class BaseTopFragment : Fragment {

    constructor(): super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            isClickable = true
            isFocusable = true
        }
    }
}