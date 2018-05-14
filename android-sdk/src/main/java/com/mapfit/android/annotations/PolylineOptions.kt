package com.mapfit.android.annotations

import com.mapfit.android.geometry.LatLng

class PolylineOptions : PolyPointAnnotationOptions<PolylineOptions>() {

    internal var points = emptyList<LatLng>()
    internal var lineCapType: CapType = CapType.BOUND

    init {
        drawOrder = 503
    }

    /**
     * Sets the points for the polyline.
     *
     * @param line
     */
    fun points(points: List<LatLng>): PolylineOptions {
        this.points = points
        return this
    }

    /**
     * Sets the shape type for the end of the lines.
     *
     * @param capType
     */
    fun lineCapType(capType: CapType): PolylineOptions {
        this.lineCapType = capType
        return this
    }


}