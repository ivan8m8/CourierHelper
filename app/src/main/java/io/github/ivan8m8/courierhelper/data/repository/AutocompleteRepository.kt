package io.github.ivan8m8.courierhelper.data.repository

import io.github.ivan8m8.courierhelper.data.network.KladrApi
import io.reactivex.rxjava3.core.Single

class AutocompleteRepository(
    private val kladrApi: KladrApi
) {
    fun autocompleteAddress(query: String): Single<List<String>> {
        return kladrApi.queryOneString(query)
            .map { response ->
                response.result?.map { it.fullName } ?: emptyList()
            }
    }
}