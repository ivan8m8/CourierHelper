package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.models.GeoTreeModels.Address
import io.github.ivan8m8.courierhelper.data.network.GeoTreeApi
import io.reactivex.rxjava3.core.Single

class GeoCodeRepository(
    private val geoTreeApi: GeoTreeApi
) {

    fun decode(address: String): Single<Address> {
        //todo: on error switch to another API
        return geoTreeApi.decode(address)
            .map { result ->
                result.first()
            }
    }
}