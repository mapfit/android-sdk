package com.mapfit.android.annotations


class BuildingOptions : PolyPointAnnotationOptions<BuildingOptions>() {

    internal var fillColor: String = ""

    init {
        drawOrder = 600
    }

    /**
     * Sets the fill color of the polygon.
     *
     * @param color as hex like "#ff00ff"
     */
    fun fillColor(color: String): BuildingOptions {
        this.fillColor = color
        return this
    }

}