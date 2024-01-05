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
    /**
     * @param initScreen is normally a lambda that sets the root screen.
     * Usually it's something like this:
     *
     * ```
     * if (savedInstanceState == null) {
     *     supportFragmentManager
     *         .beginTransaction()
     *         .replace(R.id.mainContainer, MainFragment.newInstance())
     *         .commit()
     * }
     * ```
     *
     * The main intention behind this is to couple root screen setting
     * to `NavigatorHolder`.
     */
    @CallSuper
    fun onCreate(
        navigator: Navigator,
        initScreen: () -> Unit = {}
    ) {
        setNavigator(navigator)
        initScreen()
    }
    fun onResume()
    fun onPause()
    @CallSuper
    fun onDestroy() {
        removeNavigator()
    }
}