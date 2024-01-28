package io.github.ivan8m8.courierhelper.navigation

import com.jakewharton.rxrelay3.PublishRelay
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.models.PriorityCity

/**
 * Must be a singleton.
 */
class EventBus {

    private val _addDeliveryClicked = PublishRelay.create<Unit>()
    val addDeliveryClicked get() = _addDeliveryClicked.hide()
    private val _deliveryAdded = PublishRelay.create<Delivery>()
    val deliveryAdded get() = _deliveryAdded.hide()
    private val _priorityCityClicked = PublishRelay.create<Unit>()
    val priorityCityClicked get() = _priorityCityClicked.hide()
    private val _priorityCityChosen = PublishRelay.create<PriorityCity>()
    val priorityCityChosen = _priorityCityChosen.hide()

    fun addDeliveryClicked() = _addDeliveryClicked.accept(Unit)
    fun deliveryAdded(delivery: Delivery) = _deliveryAdded.accept(delivery)
    fun priorityCityClicked() = _priorityCityClicked.accept(Unit)
    fun priorityCityChosen(city: PriorityCity) = _priorityCityChosen.accept(city)
}