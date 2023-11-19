package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.models.DadataModels.Bound
import io.github.ivan8m8.courierhelper.data.models.DadataModels.LocationBoost
import io.github.ivan8m8.courierhelper.data.models.DadataModels.RequestBody
import io.github.ivan8m8.courierhelper.data.models.DadataModels.SuggestionsResponse
import io.github.ivan8m8.courierhelper.data.network.DadataApi
import io.reactivex.rxjava3.core.Single

class AutocompleteRepository(
    private val dadataApi: DadataApi
) {

    fun autocompleteAddress(
        query: String,
        priorities: List<String>? = null
    ): Single<SuggestionsResponse> {
        val locationBoost = priorities
            ?.map { id ->
                LocationBoost(id)
            }
        return dadataApi.suggestions(
            RequestBody(
                query,
                locationBoost
            )
        )
    }

    fun autocompleteCity(
        query: String
    ) : Single<SuggestionsResponse> {
        val bound = Bound("city")
        return dadataApi.suggestions(
            RequestBody(
                query,
                fromBound = bound,
                toBound = bound
            )
        )
    }
}