package io.github.ivan8m8.courierhelper.data.models

object KladrModels {
    data class RegionResponse(
        val result: List<Region>?
    )
    data class AddressResponse(
        val result: List<Address>?
    )
    interface Suggestion {
        val id: String
    }
    data class Region(
        override val id: String,
        val name: String,
        val type: String
    ): Suggestion
    data class Address(
        override val id: String,
        val fullName: String
    ): Suggestion
}