package io.github.ivan8m8.courierhelper.data.network

import io.github.ivan8m8.courierhelper.data.models.GeoTreeModels.Address
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoTreeApi {

    @GET("address.php")
    fun decode(
        @Query("term") address: String,
        @Query("limit") limit: Int = 1
    ): Single<List<Address>>

    companion object {
        const val BASE_URL = "https://api.geotree.ru/"
    }
}