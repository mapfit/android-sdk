@file:JvmName("GeoUtils")

package com.mapfit.android.geometry

import android.graphics.PointF


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
 * Calculates and returns the screen position for the LatLng value.
 *
 * @param zoomLevel
 */
fun LatLng.toPointF(zoomLevel: Float): PointF {
    val worldWidth = toWorldWidthPixels(zoomLevel)

    val x = lng / 360 + .5
    val sinY = Math.sin(Math.toRadians(lat))
    val y = 0.5 * Math.log((1 + sinY) / (1 - sinY)) / -(2 * Math.PI) + .5

    return PointF((x * worldWidth).toFloat(), (y * worldWidth).toFloat())
}

/**
 * Calculates and returns the LatLng value for the screen position.
 *
 * @param zoomLevel
 */
fun PointF.toLatLng(zoomLevel: Float): LatLng {
    val worldWidth = toWorldWidthPixels(zoomLevel)

    val x = x / worldWidth - 0.5
    val lng = x * 360

    val y = .5 - y / worldWidth
    val lat = 90 - Math.toDegrees(Math.atan(Math.exp(-y * 2.0 * Math.PI)) * 2)

    return LatLng(lat, lng)
}

/**
 * Calculates world width for the given zoom level.
 *
 * @param zoomLevel
 */
fun toWorldWidthPixels(zoomLevel: Float) = (256 * Math.pow(2.0, zoomLevel.toDouble())).toFloat()

/**
 * Calculates the middle angle between 3 coordinates.
 *
 * @param p1 first point
 * @param p2 middle point
 * @param p3 last point
 */
fun calculateMidAngle(
    p1: LatLng,
    p2: LatLng,
    p3: LatLng
): Double {
    val numerator =
        p2.lng * (p1.lat - p3.lat) + p1.lng * (p3.lat - p2.lat) + p3.lng * (p2.lat - p1.lat)
    val denominator = (p2.lat - p1.lat) * (p1.lat - p3.lat) + (p2.lng - p1.lng) * (p1.lng - p3.lng)
    val ratio = numerator / denominator

    val angleRad = Math.atan(ratio)
    var angleDeg = angleRad * 180 / Math.PI

    if (angleDeg < 0) {
        angleDeg += 180
    }

    return angleDeg
}
