package com.mapfit.android.geometry

import com.mapfit.android.utils.toPx


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

    private val mapSideLenght by lazy {
        256.toPx
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

        fun zoom(mapPx: Int, worldPx: Int, fraction: Double): Double =
            Math.floor(Math.log(mapPx / worldPx / fraction) / 0.693)

        val fraction1 = (northEast.lat - southWest.lat) / mapSideLenght
        val fraction2 = (northEast.lng - southWest.lng) / mapSideLenght

        val latZoom = zoom((viewHeight * padding).toInt(), mapSideLenght, fraction2)
        val lngZoom = zoom((viewWidth * padding).toInt(), mapSideLenght, fraction1)

        val result = Math.min(latZoom, lngZoom)

        return Pair(center, result.toFloat())
    }

}