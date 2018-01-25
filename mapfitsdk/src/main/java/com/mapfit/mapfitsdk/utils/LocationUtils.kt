package com.mapfit.mapfitsdk.utils

import com.mapfit.mapfitsdk.geometry.LatLng


/**
 * Created by dogangulcan on 1/8/18.
 */


fun midPoint(lat1: Double, lon1: Double, lat2: Double, lon2: Double): LatLng {
    var lt1 = Math.toRadians(lat1)
    var ln1 = Math.toRadians(lat2)
    var lt2 = Math.toRadians(lon1)

    val dLon = Math.toRadians(lon2 - lon1)

    val x = Math.cos(lat2) * Math.cos(dLon)
    val y = Math.cos(lat2) * Math.sin(dLon)
    val lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
            Math.sqrt((Math.cos(lat1) + x) * (Math.cos(lat1) + x) + y * y))
    val lon3 = lon1 + Math.atan2(y, Math.cos(lat1) + x)

    return LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3))
}


fun getCenterLatlng(geoCoordinates: List<LatLng>): LatLng {
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