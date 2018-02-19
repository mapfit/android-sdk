package com.mapfit.mapfitsdk.geometry

import android.content.res.Resources
import com.mapfit.mapfitsdk.utils.toPx


/**
 * Created by dogangulcan on 1/4/18.
 */
class LatLngBounds(
    var northEast: LatLng = LatLng(),
    var southWest: LatLng = LatLng()
) {

    val center by lazy {
        getCenterLatlng(listOf(northEast, southWest))
    }

    class Builder {

        private val latLngList = mutableListOf<LatLng>()

        fun include(latLng: LatLng) {
            latLngList.add(latLng)
        }

        fun build(): LatLngBounds {
            var south: Double? = null
            var west: Double? = null
            var north: Double? = null
            var east: Double? = null

            latLngList.forEach {
                if (south == null || south!! > it.lat) south = it.lat
                if (west == null || west!! > it.lon) west = it.lon
                if (north == null || north!! < it.lat) north = it.lat
                if (east == null || east!! < it.lon) east = it.lon
            }

            val northEast = LatLng(north ?: 0.0, east ?: 0.0)
            val southWest = LatLng(south ?: 0.0, west ?: 0.0)

            return LatLngBounds(northEast, southWest)
        }
    }

    fun getVisibleBounds(viewWidth: Int, viewHeight: Int, padding: Float): Pair<LatLng, Float> {

        fun latRad(lat: Double): Double {
            val sin = Math.sin(lat * Math.PI / 180)
            val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
            return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
        }

        fun zoom(mapPx: Int, worldPx: Int, fraction: Double): Double {
            return Math.floor(Math.log(mapPx / worldPx / fraction) / 0.693)
        }

        val latFraction = (latRad(northEast.lat) - latRad(southWest.lat)) / Math.PI

        val lngDiff = northEast.lon - southWest.lon
        val lngFraction = (if (lngDiff < 0) (lngDiff + 360) else lngDiff) / 360

        val latZoom = zoom((viewHeight * padding).toInt(), 256.toPx, latFraction)
        val lngZoom = zoom((viewWidth * padding).toInt(), 256.toPx, lngFraction)

        val result = Math.min(latZoom, lngZoom)
//
        return Pair(center, result.toFloat())
    }

}