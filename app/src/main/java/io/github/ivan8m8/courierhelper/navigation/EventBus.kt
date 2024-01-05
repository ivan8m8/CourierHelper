package io.github.ivan8m8.courierhelper.navigation

import com.jakewharton.rxrelay3.PublishRelay
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.PriorityCity

/**
 * Must be a singleton.
 */
class EventBus {

    val addDeliveryClicked = PublishRelay.create<Unit>()
    val deliveryAdded = PublishRelay.create<Delivery>()
    val priorityCityChosen = PublishRelay.create<PriorityCity>()

    fun addDeliveryClicked() = addDeliveryClicked.accept(Unit)
    fun deliveryAdded(delivery: Delivery) = deliveryAdded.accept(delivery)
    fun priorityCityChosen(city: PriorityCity) = priorityCityChosen.accept(city)
}