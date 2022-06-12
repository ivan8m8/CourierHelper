package io.github.ivan8m8.courierhelper.data.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface KladrApi {
    @GET("api.php?oneString=1")
    fun queryOneString(
        @Query("query") query: String,
        @Query("limit") limit: Int = 5
    ): Single<QueryResponse>
    companion object {
        const val BASE_URL = "https://kladr-api.ru/"
        data class QueryResponse(val result: List<Result>?)
        data class Result(val fullName: String)
    }
}