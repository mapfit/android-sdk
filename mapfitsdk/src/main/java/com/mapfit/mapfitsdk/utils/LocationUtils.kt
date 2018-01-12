package com.mapfit.mapfitsdk.utils

import com.mapfit.mapfitsdk.geo.LatLng


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