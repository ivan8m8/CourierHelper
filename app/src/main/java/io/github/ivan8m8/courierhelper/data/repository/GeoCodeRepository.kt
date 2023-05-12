package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.models.GeoTreeModels.Address
import io.github.ivan8m8.courierhelper.data.network.GeoTreeApi
import io.reactivex.rxjava3.core.Single

class GeoCodeRepository(
    private val geoTreeApi: GeoTreeApi
) {

    /**
     * The result list may be empty. The API user must decide
     * how to handle such case.
     */
    fun decode(address: String): Single<List<Address>> {
        return geoTreeApi.decode(address)
    }
}