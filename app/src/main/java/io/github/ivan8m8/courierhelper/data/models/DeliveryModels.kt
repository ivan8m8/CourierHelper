package io.github.ivan8m8.courierhelper.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.ivan8m8.courierhelper.R

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

enum class PaymentMethod(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
) {
    CARD(
        iconRes = R.drawable.round_payment_24,
        nameRes = R.string.by_card
    ),
    CASH(
        iconRes = R.drawable.round_money_24,
        nameRes = R.string.by_cash
    ),
    TRANSFER(
        iconRes = R.drawable.round_p2p_24,
        nameRes = R.string.by_transfer
    ),
}

enum class DeliveryStatus {
    IN_PROGRESS,
    DELIVERED
}