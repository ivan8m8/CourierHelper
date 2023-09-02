package io.github.ivan8m8.courierhelper.data.workers

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import io.github.ivan8m8.courierhelper.data.repository.MetroRepository
import io.reactivex.rxjava3.core.Single

class FetchMetroStationsWorker(
    private val metroRepository: MetroRepository,
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {
    override fun createWork(): Single<Result> {
        return metroRepository.fetchAllMetroStationsFromServer()
            .map { Result.success() }
    }
}