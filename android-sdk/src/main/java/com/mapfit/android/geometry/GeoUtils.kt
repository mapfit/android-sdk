@file:JvmName("GeoUtils")

package com.mapfit.android.geometry

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Returns the unweighted center for a list of [LatLng].
 *
 * @param coordinates
 * @return [LatLng] center of the given coordinates
 */
fun getCenterLatLng(coordinates: List<LatLng>): LatLng {
    if (coordinates.size == 1) {
        return coordinates.first()
    }

    var x = 0.0
    var y = 0.0
    var z = 0.0

    coordinates.forEach { latLng ->
        val latitude = Math.toRadians(latLng.lat)
        val longitude = Math.toRadians(latLng.lng)

        x += Math.cos(latitude) * Math.cos(longitude)
        y += Math.cos(latitude) * Math.sin(longitude)
        z += Math.sin(latitude)
    }

    val total = coordinates.size

    x /= total
    y /= total
    z /= total

    val centralLongitude = Math.atan2(y, x)
    val centralSquareRoot = Math.sqrt(x * x + y * y)
    val centralLatitude = Math.atan2(z, centralSquareRoot)

    return LatLng(Math.toDegrees(centralLatitude), Math.toDegrees(centralLongitude))

}

/**
 * Returns the middle point of two coordinates.
 *
 * @param first
 * @param second
 * @return [LatLng] mid point of first and second
 */
fun midPoint(first: LatLng, second: LatLng): LatLng {
    val dLon = Math.toRadians(second.lng - first.lng)

    val radLat1 = Math.toRadians(first.lat)
    val radLat2 = Math.toRadians(second.lat)
    val radLon1 = Math.toRadians(first.lng)

    val bX = Math.cos(radLat2) * Math.cos(dLon)
    val bY = Math.cos(radLat2) * Math.sin(dLon)
    val lat3 = Math.atan2(
        Math.sin(radLat1) + Math.sin(radLat2),
        Math.sqrt((Math.cos(radLat1) + bX) * (Math.cos(radLat1) + bX) + bY * bY)
    )
    val lon3 = radLon1 + Math.atan2(bY, Math.cos(radLat1) + bX)

    return LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3))
}

/**
 * Calculates and returns earth radius for given latitude.
 *
 * @param lat
 * @return earth radius
 */
fun getEarthRadiusForLat(lat: Double): Double {
    val a = 6378137.0 // semi major radius
    val b = 6356752.314245 // semi minor radius

    val an = sqrt(a) * cos(lat)
    val bn = sqrt(b) * sin(lat)
    val ad = a * cos(lat)
    val bd = b * sin(lat)

    return sqrt(sqrt(an) + sqrt(bn) / (sqrt(ad) + sqrt(bd)))
}


