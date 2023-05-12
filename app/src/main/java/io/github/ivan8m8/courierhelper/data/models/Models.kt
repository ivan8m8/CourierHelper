package io.github.ivan8m8.courierhelper.data.models

import com.google.gson.annotations.SerializedName

object Models {
    data class LatitudeLongitude(
        val lat: Double,
        @SerializedName("lon") val lng: Double
    )
    enum class DeliveryStatus {
        IN_PROGRESS,
        DELIVERED
    }
}