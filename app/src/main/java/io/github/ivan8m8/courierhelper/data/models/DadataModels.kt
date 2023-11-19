package io.github.ivan8m8.courierhelper.data.models

import com.google.gson.annotations.SerializedName

object DadataModels {

    data class Bound(
        val value: String
    )

    data class LocationBoost(
        @SerializedName("kladr_id") val id: String
    )

    data class RequestBody(
        val query: String,
        @SerializedName("locations_boost") val locationBoost: List<LocationBoost>? = null,
        @SerializedName("from_bound") val fromBound: Bound? = null,
        @SerializedName("to_bound") val toBound: Bound? = null,
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