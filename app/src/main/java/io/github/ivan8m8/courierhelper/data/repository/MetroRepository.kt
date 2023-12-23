package io.github.ivan8m8.courierhelper.data.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.ivan8m8.courierhelper.data.db.daos.MetroStationsDao
import io.github.ivan8m8.courierhelper.data.models.MetroStation
import io.github.ivan8m8.courierhelper.data.models.LatitudeLongitude
import io.github.ivan8m8.courierhelper.data.network.HhApi
import io.github.ivan8m8.courierhelper.data.utils.MetroUtils
import io.github.ivan8m8.courierhelper.data.workers.FetchMetroStationsWorker
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MetroRepository(
    workManager: WorkManager,
    private val metroUtils: MetroUtils,
    private val metroStationsDao: MetroStationsDao,
    private val hhApi: HhApi
) {

    init {
        workManager.enqueueUniquePeriodicWork(
            FetchMetroStationsWorker::class.java.simpleName,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<FetchMetroStationsWorker>(3, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    fun getClosestStations(
        latLng: LatitudeLongitude,
        limit: Int = 2
    ): Single<List<Pair<Double, MetroStation>>> {
        return Single.fromCallable {
            metroUtils.getClosestArea(latLng)
        }
            .flatMap { area ->
                metroStationsDao.getInArea(
                    area.south.lat,
                    area.north.lat,
                    area.west.lng,
                    area.east.lng
                )
            }
            .map { stations ->
                stations
                    .associateBy { station ->
                        val stationLatLng = LatitudeLongitude(station.lat, station.lng)
                        metroUtils.distanceBetweenPoints(stationLatLng, latLng)
                    }
                    .toList()
                    .sortedBy { it.first }
                    .take(limit)
            }
    }

    fun fetchAllMetroStationsFromServer(): Single<List<MetroStation>> {
        return hhApi.getAllMetroStations()
            .map { cities -> cities.map { city -> city.lines }.flatten() }
            .map { lines -> lines.map { line -> line.stations }.flatten() }
            .flatMap { stations ->
                metroStationsDao.insert(stations)
                    .toSingle { stations }
            }
            .subscribeOn(Schedulers.io())
    }
}