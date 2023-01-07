package io.github.ivan8m8.courierhelper.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.ivan8m8.courierhelper.data.models.Models.LatitudeLongitude

@Entity
data class Delivery(
    val address: String,
    val latLng: LatitudeLongitude? = null,
    val phoneNumber: String? = null,
    val orderNumber: String? = null,
    val itemPrice: Double? = null,
    val itemName: String? = null,
    val clientName: String? = null,
    val comment: String? = null,
    val status: Models.DeliveryStatus = Models.DeliveryStatus.IN_PROGRESS,
    val added: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)