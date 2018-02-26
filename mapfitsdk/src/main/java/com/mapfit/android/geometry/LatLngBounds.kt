package com.mapfit.android.geometry

import android.content.res.Resources
import kotlin.math.ln


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
        getCenterLatLng(listOf(northEast, southWest))
    }

    private val mapSideLength by lazy {
        256 * Resources.getSystem().displayMetrics.density
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
     * INTERNAL USAGE ONLY.
     */
    fun getVisibleBounds(viewWidth: Int, viewHeight: Int, padding: Float): Pair<LatLng, Float> {

        fun zoom(mapPx: Int, fraction: Double): Double =
            ln((mapPx / mapSideLength / fraction) * padding) / 0.69314718056

        val latFraction =
            (StrictMath.toRadians(northEast.lat) - StrictMath.toRadians((southWest.lat))) / Math.PI

        val lngDiff = northEast.lng - southWest.lng
        val lngFraction = (if (lngDiff < 0) (lngDiff + 360) else lngDiff) / 360

        val latZoom = zoom(viewHeight, latFraction)
        val lngZoom = zoom(viewWidth, lngFraction)

        val result = Math.min(latZoom, lngZoom)

        return Pair(center, result.toFloat())
    }

}