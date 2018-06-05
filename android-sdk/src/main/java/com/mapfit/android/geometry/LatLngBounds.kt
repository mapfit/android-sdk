package com.mapfit.android.geometry

import android.content.res.Resources
import com.mapfit.android.MapOptions


/**
 * Defines a rectangle with north east and south west coordinates.
 *
 * Created by dogangulcan on 1/4/18.
 */
class LatLngBounds(
    val northEast: LatLng = LatLng(),
    val southWest: LatLng = LatLng()
) {

    val center: LatLng by lazy {
        midPoint(northEast, southWest)
    }

    private val mapSideLength by lazy {
        256 * (Resources.getSystem()?.displayMetrics?.density ?: 1f)
    }

    class Builder {

        private val latLngList = mutableListOf<LatLng>()

        /**
         * @param [LatLng] point to be included in the bounds
         */
        fun include(latLng: LatLng) {
            latLngList.add(latLng)
        }

        /**
         * Builds [LatLngBounds] with the current list of [LatLng] points.
         */
        fun build(): LatLngBounds {
            var south: Double? = null
            var west: Double? = null
            var north: Double? = null
            var east: Double? = null

            latLngList.forEach {
                if (south == null || south!! > it.lat) south = it.lat
                if (west == null || west!! > it.lng) west = it.lng
                if (north == null || north!! < it.lat) north = it.lat
                if (east == null || east!! < it.lng) east = it.lng
            }

            val northEast = LatLng(north ?: 0.0, east ?: 0.0)
            val southWest = LatLng(south ?: 0.0, west ?: 0.0)

            return LatLngBounds(northEast, southWest)
        }
    }

    /**
     * Calculates visible bounds of the map.
     *
     * @param viewWidth of the MapView
     * @param viewHeight of the MapView
     * @param padding padding for the bounds
     * @return center point and zoom level
     */
    fun getVisibleBounds(viewWidth: Int, viewHeight: Int, padding: Float): Pair<LatLng, Float> {
        val reversePadding = if ((1 - padding) == 0f) 1f else (1 - padding)

        val latFraction = (latRad(northEast.lat) - latRad(southWest.lat)) / Math.PI
        val lngDiff = northEast.lng - southWest.lng
        val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
        val latZoom = zoom(viewHeight.toDouble(), reversePadding, latFraction)
        val lngZoom = zoom(viewWidth.toDouble(), reversePadding, lngFraction)
        val zoom = Math.min(Math.min(latZoom, lngZoom), MapOptions.MAP_MAX_ZOOM)

        return Pair(center, zoom.toFloat())
    }

    private fun latRad(lat: Double): Double {
        val sin = Math.sin(lat * Math.PI / 180)
        val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
    }

    private fun zoom(mapPx: Double, padding: Float, fraction: Double): Double {
        return Math.log((mapPx / mapSideLength.toDouble() / fraction) * padding) / .693147180559945309417
    }

}