package io.github.ivan8m8.courierhelper.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import io.github.ivan8m8.courierhelper.ui.fragments.AddDeliveryFragment

class MyNavigator(
    eventBus: EventBus,
    fm: FragmentManager,
    @IdRes containerId: Int
) : Navigator(eventBus, fm, containerId) {

    override fun observe() {
        eventBus.addDeliveryClicked.onEvent {
            showScreen(AddDeliveryFragment.newInstance())
        }
        eventBus.deliveryAdded.onEvent {
            goBack()
        }
    }
}