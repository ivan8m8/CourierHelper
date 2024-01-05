package io.github.ivan8m8.courierhelper.navigation.holder

import io.github.ivan8m8.courierhelper.navigation.Navigator

class NavigatorHolder : INavigatorHolder {

    private var navigator: Navigator? = null

    override fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    override fun removeNavigator() {
        navigator = null
    }

    override fun onResume() {
        navigator?.resume()
    }

    override fun onPause() {
        navigator?.pause()
    }

    override fun onDestroy() {
        navigator?.destruct()
        super.onDestroy()
    }
}