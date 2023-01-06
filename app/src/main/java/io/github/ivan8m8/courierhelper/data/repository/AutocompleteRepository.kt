package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.models.KladrModels.Address
import io.github.ivan8m8.courierhelper.data.models.KladrModels.Region
import io.github.ivan8m8.courierhelper.data.models.KladrModels.Suggestion
import io.github.ivan8m8.courierhelper.data.network.KladrApi
import io.reactivex.rxjava3.core.Single

class AutocompleteRepository(
    private val kladrApi: KladrApi
) {

    fun autocompleteRegion(
        query: String
    ): Single<List<Region>> {
        return kladrApi.autocompleteRegion(query)
            .map { response ->
                response.result ?: emptyList()
            }
            .excludeWatermark()
    }

    fun autocompleteAddress(
        query: String,
        regionId: String
    ): Single<List<Address>> {
        return kladrApi.autocompleteAddress(query, regionId)
            .map { response ->
                response.result ?: emptyList()
            }
            .excludeWatermark()
    }

    private fun <T : Suggestion> Single<List<T>>.excludeWatermark() = this
        .map { list ->
            list.filter { item ->
                item.id != "Free"
            }
        }
}