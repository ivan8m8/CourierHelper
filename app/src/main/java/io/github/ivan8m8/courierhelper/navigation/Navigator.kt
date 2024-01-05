package io.github.ivan8m8.courierhelper.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

abstract class Navigator(
    protected val eventBus: EventBus,
    private val fm: FragmentManager,
    @IdRes private val containerId: Int
) : INavigator {

    private val disposables = CompositeDisposable()
    private var isResumed = false
    final override val buffer: ArrayList<() -> Unit> = ArrayList()

    final override fun create() {
        observe()
    }

    final override fun resume() {
        isResumed = true
    }

    final override fun pause() {
        isResumed = false
    }

    final override fun destroy() {
        disposables.dispose()
    }

    protected fun showScreen(
        fragment: Fragment,
        isReplace: Boolean = false,
        isAddToBackStack: Boolean = true
    ) {
        val transaction = fm.beginTransaction()

        if (isReplace) {
            transaction.replace(containerId, fragment)
        } else {
            transaction.add(containerId, fragment)
        }

        if (isAddToBackStack) {
            transaction.addToBackStack(fragment::class.simpleName)
        }

        // https://stackoverflow.com/questions/38566628
        transaction.commit()
    }

    protected fun goBack(noFurther: KClass<Fragment>? = null) {
        fm.popBackStackImmediate()
    }

    private fun saveEvent(event: () -> Unit) {
        buffer.add(event)
    }

    protected fun PublishRelay<*>.onEvent(
        doOnEvent: () -> Unit
    ) {
        this
            .debounce(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (isResumed) {
                        doOnEvent()
                    } else {
                        saveEvent(doOnEvent)
                    }
                }
            )
            .addTo(disposables)
    }
}