package com.mapfit.android.geometry

import android.location.Location
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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


/**
 * Calculates and returns earth radius for given latitude.
 *
 * @return earth radius
 */
internal fun getEarthRadiusForLat(lat: Double): Double {
    val a = 6378137.0 // semi major radius
    val b = 6356752.314245 // semi minor radius

    val an = sqrt(a) * cos(lat)
    val bn = sqrt(b) * sin(lat)
    val ad = a * cos(lat)
    val bd = b * sin(lat)

    return sqrt(sqrt(an) + sqrt(bn) / (sqrt(ad) + sqrt(bd)))
}

internal fun normalizeLocation(location: Location): Location {
    val pRad = getEarthRadiusForLat(location.latitude)
    val pXtt = Math.toRadians(location.latitude)
    val pYtt = Math.toRadians(location.longitude)
    val pX = pRad * Math.cos(pXtt) * Math.cos(pYtt)
    val pY = pRad * Math.cos(pXtt) * Math.sin(pYtt)
    val pZ = pRad * Math.sin(pXtt)
    val pHa = location.accuracy.toDouble()
    val pVe = location.speed.toDouble()
    val pT = location.time.toDouble()

    val lat = Math.toDegrees(Math.atan2(pZ, Math.sqrt(pX * pX + pY * pY)))
    val lon = Math.toDegrees(Math.atan2(pY, pX))

    val geoLoc = Location("")
    geoLoc.latitude = lat
    geoLoc.longitude = lon
    geoLoc.accuracy = pHa.toFloat()
    geoLoc.speed = pVe.toFloat()
    geoLoc.time = pT.toLong()

    return geoLoc

}