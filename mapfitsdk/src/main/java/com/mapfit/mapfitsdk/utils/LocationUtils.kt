package com.mapfit.mapfitsdk.utils

import com.mapfit.mapfitsdk.geometry.LatLng


/**
 * Created by dogangulcan on 1/8/18.
 */


internal fun getCenterLatlng(geoCoordinates: List<LatLng>): LatLng {
    if (geoCoordinates.size == 1) {
        return geoCoordinates.first()
    }

    var x = 0.0
    var y = 0.0
    var z = 0.0

    geoCoordinates.forEach { latLng ->
        val latitude = latLng.lat * Math.PI / 180
        val longitude = latLng.lon * Math.PI / 180

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

    return LatLng(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI)
}