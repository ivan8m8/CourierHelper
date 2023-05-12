package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.db.daos.DeliveriesDao
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.reactivex.rxjava3.core.Single

class DeliveriesRepository(
    private val deliveriesDao: DeliveriesDao
) {

    fun get(id: Long) = deliveriesDao.get(id)

    fun save(delivery: Delivery): Single<Long> {
        return deliveriesDao.insert(delivery)
    }
}