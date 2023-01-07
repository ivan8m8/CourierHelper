package io.github.ivan8m8.courierhelper.data.repository

import android.content.Context
import androidx.work.*
import io.github.ivan8m8.courierhelper.data.db.daos.DeliveriesDao
import io.github.ivan8m8.courierhelper.data.models.Delivery
import io.github.ivan8m8.courierhelper.data.workers.GeoCodeWorker
import io.reactivex.rxjava3.core.Completable

class DeliveriesRepository(
    private val deliveriesDao: DeliveriesDao,
    private val appContext: Context
) {

    fun get(id: Int) = deliveriesDao.get(id)

    fun save(delivery: Delivery): Completable {
        return deliveriesDao.insert(delivery)
            .andThen {
                WorkManager.getInstance(appContext)
                    .enqueue(
                        OneTimeWorkRequestBuilder<GeoCodeWorker>()
                            .setInputData(
                                Data(
                                    mapOf(
                                        GeoCodeWorker.DELIVERY_ID_KEY to delivery.id
                                    )
                                )
                            )
                            .setConstraints(
                                Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                            )
                            .build()
                    )
            }
    }
}