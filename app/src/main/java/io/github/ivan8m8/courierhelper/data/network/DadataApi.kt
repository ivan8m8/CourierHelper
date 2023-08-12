package io.github.ivan8m8.courierhelper.data.network

import io.github.ivan8m8.courierhelper.data.models.DadataModels.RequestBody
import io.github.ivan8m8.courierhelper.data.models.DadataModels.SuggestionsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface DadataApi {

    @POST("suggestions/api/4_1/rs/suggest/address")
    fun suggestions(
        @Body body: RequestBody
    ): Single<SuggestionsResponse>

    companion object {
        const val BASE_URL = "https://suggestions.dadata.ru/"
    }
}