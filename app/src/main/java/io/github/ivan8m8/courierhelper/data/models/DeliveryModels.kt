package io.github.ivan8m8.courierhelper.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Delivery(
    @Embedded val address: DeliveryAddress,
    val metro: String? = null,
    /**
     * RRGGBB
     */
    val metroColor: String? = null,
    /**
     * Distance to the closest metro in meters.
     */
    val metroDistance: Double? = null,
    val metro2: String? = null,
    /**
     * RRGGBB
     */
    val metro2Color: String? = null,
    /**
     * Distance to the second-closest metro in meters.
     */
    val metro2Distance: Double? = null,
    val phoneNumber: String? = null,
    val orderNumber: String? = null,
    val orderPrice: Double? = null,
    val itemName: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val clientName: String? = null,
    val comment: String? = null,
    val status: DeliveryStatus = DeliveryStatus.IN_PROGRESS,
    val added: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)

data class DeliveryAddress(
    val full: String,
    val city: String,
    val street: String?,
    val house: String?,
    val flat: String?,
    @Embedded val latLng: LatitudeLongitude,
)

enum class PaymentMethod {
    CARD,
    CASH,
    TRANSFER
}

enum class DeliveryStatus {
    IN_PROGRESS,
    DELIVERED
}