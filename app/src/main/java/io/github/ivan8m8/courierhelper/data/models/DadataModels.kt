package io.github.ivan8m8.courierhelper.data.models

import com.google.gson.annotations.SerializedName

object DadataModels {

    data class RequestBody(
        val query: String,
        @SerializedName("count") val limit: Int = 5
    )

    data class SuggestionData(
        @SerializedName("geo_lat") val lat: Double,
        @SerializedName("geo_lon") val lng: Double
    )

    data class Suggestion(
        val value: String,
        val data: SuggestionData
    )

    data class SuggestionsResponse(
        val suggestions: List<Suggestion>
    )
}