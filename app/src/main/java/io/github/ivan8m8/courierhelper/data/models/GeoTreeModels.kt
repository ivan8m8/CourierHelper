package io.github.ivan8m8.courierhelper.data.models

import com.google.gson.annotations.SerializedName
import io.github.ivan8m8.courierhelper.data.models.Models.LatitudeLongitude

object GeoTreeModels {
    data class Address(
        @SerializedName("geo_inside") val latLng: LatitudeLongitude
    )
}