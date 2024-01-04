package io.github.ivan8m8.courierhelper.navigation.holder

import androidx.annotation.CallSuper
import io.github.ivan8m8.courierhelper.navigation.Navigator

/**
 * A singleton proxy for convenience: allows to work with a not
 * nullable `Navigator` by encapsulating that nullability here.
 * As well, all communication with `Activity` is encapsulated only here.
 */
interface INavigatorHolder {

    // Core functionality
    fun setNavigator(navigator: Navigator)
    fun removeNavigator()

    // Activity's lifecycle
    @CallSuper
    fun onCreate(navigator: Navigator) {
        setNavigator(navigator)
    }
    fun onResume()
    fun onPause()
    @CallSuper
    fun onDestroy() {
        removeNavigator()
    }
}