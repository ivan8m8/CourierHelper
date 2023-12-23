package io.github.ivan8m8.courierhelper.data.utils

import io.github.ivan8m8.courierhelper.data.models.LatitudeLongitude
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

//https://www.frameworks.su/article/rasstoyanie_do_bligayshih_stantsii_metro
class MetroUtils {

    fun distanceBetweenPoints(
        one: LatitudeLongitude,
        other: LatitudeLongitude
    ): Double {
        return acos(
            sin(one.lat.toRadians()) * sin(other.lat.toRadians())
            + cos(one.lat.toRadians()) * cos(other.lat.toRadians())
            * cos(one.lng.toRadians() - other.lng.toRadians())
        ) * EARTH_RADIUS
    }

    fun getClosestArea(
        latLng: LatitudeLongitude,
        distanceM: Int = 2000
    ): ClosestArea {
        val east = getAreaPoint(latLng, 0.0, distanceM)
        val north = getAreaPoint(latLng, 90.0, distanceM)
        val west = getAreaPoint(latLng, 180.0, distanceM)
        val south = getAreaPoint(latLng, 270.0, distanceM)
        return ClosestArea(west, north, east, south)
    }

    private fun getAreaPoint(
        latLng: LatitudeLongitude,
        angleDegree: Double,
        distanceM: Int
    ): LatitudeLongitude {
        val angleRad = angleDegree.toRadians()
        val dx = distanceM / (LAT_1_DEGREE_LENGTH * cos(latLng.lat.toRadians())) * cos(angleRad)
        val dy = distanceM / LAT_1_DEGREE_LENGTH * sin(angleRad)
        return LatitudeLongitude(
            latLng.lat + dy,
            latLng.lng + dx
        )
    }

    companion object {

        data class ClosestArea(
            val west: LatitudeLongitude,
            val north: LatitudeLongitude,
            val east: LatitudeLongitude,
            val south: LatitudeLongitude
        )

        /**
         * Meridian length of 1 degree of latitude on the sphere in meters.
         */
        private const val LAT_1_DEGREE_LENGTH: Double = 40075 * 1000 / 360.0

        private const val EARTH_RADIUS = 6371.0072
    }
}