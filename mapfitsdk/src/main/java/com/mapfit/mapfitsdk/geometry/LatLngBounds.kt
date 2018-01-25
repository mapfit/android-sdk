package com.mapfit.mapfitsdk.geometry

import android.view.View
import com.mapfit.mapfitsdk.utils.getCenterLatlng


/**
 * Created by dogangulcan on 1/4/18.
 */
class LatLngBounds(
        var nortEast: LatLng = LatLng(),
        var southWest: LatLng = LatLng()
) {

    val center by lazy {
        getCenterLatlng(listOf(nortEast, southWest))
    }

    private constructor(builder: Builder) : this() {

        var south: Double? = null
        var west: Double? = null
        var north: Double? = null
        var east: Double? = null

        builder.latLngList.forEach {
            if (south == null || south!! > it.lat) south = it.lat
            if (west == null || west!! > it.lon) west = it.lon
            if (north == null || north!! < it.lat) north = it.lat
            if (east == null || east!! < it.lon) east = it.lon
        }

        nortEast = LatLng(north ?: 0.0, east ?: 0.0)
        southWest = LatLng(south ?: 0.0, west ?: 0.0)
    }

    class Builder {

        internal val latLngList = mutableListOf<LatLng>()

        fun include(latLng: LatLng) {
            latLngList.add(latLng)
        }

        fun build() = LatLngBounds(this)
    }

    fun getZoomLevel(mapView: View): Float {
        val (WORLD_HEIGHT, WORLD_WIDTH) = Pair(512, 512)
        val latFraction = (Math.toRadians(nortEast.lat) - Math.toRadians(southWest.lat)) / Math.PI
        val lngDiff = nortEast.lon - southWest.lon
        val lngFraction = if (lngDiff < 0) {
            lngDiff + 360
        } else {
            lngDiff / 360
        }

        val latZoom = Math.log(mapView.height / WORLD_HEIGHT / latFraction) / .693147180559945309417
        val lngZoom = Math.log(mapView.width / WORLD_WIDTH / lngFraction) / .693147180559945309417

//        return Math.floor(Math.min(latZoom, lngZoom)).toFloat()
        return (Math.min(latZoom, lngZoom)).toFloat()
    }

    fun getVisibleBounds(mapView: View): Pair<LatLng, Float> {
        val ry1 = Math.log((Math.sin(Math.toRadians(southWest.lat)) + 1) / Math.cos(Math.toRadians(southWest.lat)))
        val ry2 = Math.log((Math.sin(Math.toRadians(nortEast.lat)) + 1) / Math.cos(Math.toRadians(nortEast.lat)))
        val ryc = (ry1 + ry2) / 2
        val centerX = Math.toDegrees(Math.atan(Math.sinh(ryc)))

        val resolutionHorizontal = (nortEast.lon - southWest.lon) / mapView.width

        val vy0 = Math.log(Math.tan(Math.PI * (0.25 + centerX / 360)))
        val vy1 = Math.log(Math.tan(Math.PI * (0.25 + nortEast.lat / 360)))
        val viewHeightHalf = mapView.height / 2.0f
        val zoomFactorPowered = viewHeightHalf / (40.7436654315252 * (vy1 - vy0))
        val resolutionVertical = 360.0 / (zoomFactorPowered * 256)

        val paddingFactor = 1.9
        val resolution = Math.max(resolutionHorizontal, resolutionVertical) * paddingFactor
        val zoom = kotlin.math.log(360 / (resolution * 512), 2.0)

        val lon = center.lon

//        return Pair(LatLng(centerX, lon), zoom.toFloat())
        return Pair(center, zoom.toFloat())
    }


}