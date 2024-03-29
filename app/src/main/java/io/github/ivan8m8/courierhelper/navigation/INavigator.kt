package io.github.ivan8m8.courierhelper.navigation

interface INavigator {

    /**
     * Events that have not had time to happen before `onPause()`.
     */
    val buffer: ArrayList<*>

    // Lifecycle
    fun create()
    fun resume()
    fun pause()
    fun destroy()

    fun observe()
}