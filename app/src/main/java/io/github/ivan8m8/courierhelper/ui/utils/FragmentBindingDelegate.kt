package io.github.ivan8m8.courierhelper.ui.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// https://zhuinden.medium.com/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c
// https://github.com/Zhuinden/fragmentviewbindingdelegate-kt

class FragmentBindingDelegate<T: ViewBinding>(
    fragment: Fragment,
    private val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {

    private var _binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val observer = Observer<LifecycleOwner?> { owner ->
                if (owner == null) {
                    _binding = null
                }
            }
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(observer)
            }
            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(observer)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val view = thisRef.view
        val binding = _binding
        if (binding != null && binding.root === view) {
            return binding
        }

        if (view == null) {
            throw IllegalStateException("Must not attempt to get a binding when the Fragment's view is null")
        }

        return viewBindingFactory(view)
            .also { _binding = it }
    }
}

fun <T: ViewBinding> Fragment.viewBinding(factory: (View) -> T) =
    FragmentBindingDelegate(this, factory)