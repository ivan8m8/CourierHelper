package io.github.ivan8m8.courierhelper.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class CountryData(
    val phoneCode: Int,
    val iso2Name: String,
)

@Entity
data class PriorityCity(
    @PrimaryKey val kladrId: String,
    val name: String
)

data class LatitudeLongitude(
    val lat: Double,
    val lng: Double
)