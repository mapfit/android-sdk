package com.mapfit.android.annotations

import com.mapfit.android.geometry.LatLng

class PolygonOptions : PolyPointAnnotationOptions<PolygonOptions>() {

    internal var points = emptyList<List<LatLng>>()
    internal var fillColor: String = ""

    init {
        drawOrder = 502
    }

    /**
     * Sets the points of the polygon.
     *
     * @param points list of polygon rings
     */
    fun points(points: List<List<LatLng>>): PolygonOptions {
        this.points = points
        return this
    }

    /**
     * Sets the fill color of the polygon.
     *
     * @param color as hex like "#ff00ff"
     */
    fun fillColor(color: String): PolygonOptions {
        this.fillColor = color
        return this
    }

}