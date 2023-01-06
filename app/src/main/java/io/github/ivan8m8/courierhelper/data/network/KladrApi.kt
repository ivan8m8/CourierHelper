package io.github.ivan8m8.courierhelper.data.network

import io.github.ivan8m8.courierhelper.data.models.KladrModels.AddressResponse
import io.github.ivan8m8.courierhelper.data.models.KladrModels.RegionResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface KladrApi {

    @GET("api.php?contentType=region")
    fun autocompleteRegion(
        @Query("query") query: String,
        @Query("limit") limit: Int = 5
    ): Single<RegionResponse>

    @GET("api.php?oneString=1")
    fun autocompleteAddress(
        @Query("query") query: String,
        @Query("regionId") regionId: String,
        @Query("limit") limit: Int = 5
    ): Single<AddressResponse>

    companion object {
        const val BASE_URL = "https://kladr-api.ru/"
    }
}