package io.github.ivan8m8.courierhelper.data.network

import io.github.ivan8m8.courierhelper.data.models.HhModels.MetroCity
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface HhApi {

    @GET("metro")
    fun getAllMetroStations(): Single<List<MetroCity>>

    companion object {
        const val BASE_URL = "https://api.hh.ru/"
    }
}