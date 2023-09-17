package io.github.ivan8m8.courierhelper.data.utils

import android.content.Context
import androidx.annotation.StringRes

open class BaseMapper(
    protected val context: Context
) {

    protected fun getString(@StringRes resId: Int) = context.getString(resId)
}