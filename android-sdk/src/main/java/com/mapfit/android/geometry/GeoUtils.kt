package com.mapfit.android.geometry


/**
 * Created by dogangulcan on 1/8/18.
 */


internal fun getCenterLatLng(geoCoordinates: List<LatLng>): LatLng {
    if (geoCoordinates.size == 1) {
        return geoCoordinates.first()
    }

    var x = 0.0
    var y = 0.0
    var z = 0.0

    geoCoordinates.forEach { latLng ->
        val latitude = Math.toRadians(latLng.lat)
        val longitude = Math.toRadians(latLng.lng)

        x += Math.cos(latitude) * Math.cos(longitude)
        y += Math.cos(latitude) * Math.sin(longitude)
        z += Math.sin(latitude)
    }

    val total = geoCoordinates.size

    x /= total
    y /= total
    z /= total

    val centralLongitude = Math.atan2(y, x)
    val centralSquareRoot = Math.sqrt(x * x + y * y)
    val centralLatitude = Math.atan2(z, centralSquareRoot)

    return LatLng(Math.toDegrees(centralLatitude), Math.toDegrees(centralLongitude))

}