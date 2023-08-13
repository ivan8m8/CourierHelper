package io.github.ivan8m8.courierhelper.data.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

object HhModels {

    data class MetroStationLine(
        val id: String,
        @SerializedName("hex_color") val color: String,
        val name: String
    )

    @Entity
    data class MetroStation(
        val id: String,
        val name: String,
        val lat: Double,
        val lng: Double,
        val line: MetroStationLine
    )

    data class MetroLine(
        val id: String,
        @SerializedName("hex_color") val color: String, // RRGGBB
        val name: String,
        val stations: List<MetroStation>
    )

    data class MetroCity(
        val id: String,
        val name: String,
        val lines: List<MetroLine>
    )
}