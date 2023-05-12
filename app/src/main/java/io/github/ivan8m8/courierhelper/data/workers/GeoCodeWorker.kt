package io.github.ivan8m8.courierhelper.data.workers

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import io.github.ivan8m8.courierhelper.data.repository.DeliveriesRepository
import io.github.ivan8m8.courierhelper.data.repository.GeoCodeRepository
import io.reactivex.rxjava3.core.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GeoCodeWorker(
    appContext: Context,
    workerParams: WorkerParameters
): RxWorker(appContext, workerParams), KoinComponent {

    override fun createWork(): Single<Result> {

        val tmp = -1L
        val deliveryId = inputData.getLong(DELIVERY_ID_KEY, tmp)
        if (deliveryId == tmp)
            return Single.just(Result.failure())

        val geoCodeRepository: GeoCodeRepository = get()
        val deliveriesRepository: DeliveriesRepository = get()
        return deliveriesRepository.get(deliveryId)
            .firstOrError()
            .flatMap { delivery ->
                geoCodeRepository.decode(delivery.address)
                    .map { result ->
                        result.first()
                    }
                    .map { result ->
                        result.latLng
                    }
                    .flatMapCompletable { latLng ->
                        deliveriesRepository.save(delivery.copy(latLng = latLng))
                    }
                    .toSingle { Result.success() }
                    .onErrorReturn { throwable ->
                        if (throwable is NoSuchElementException)
                            Result.failure()
                        else
                            throw throwable
                    }
            }
            .onErrorReturn { throwable ->
                if (throwable is NoSuchElementException)
                    Result.failure()
                else
                    throw throwable
            }
    }

    companion object {
        const val DELIVERY_ID_KEY = "delivery_id_key"
    }
}