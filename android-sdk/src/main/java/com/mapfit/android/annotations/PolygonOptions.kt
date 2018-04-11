package com.mapfit.android.annotations

import com.mapfit.android.MapController

class PolygonOptions internal constructor(
    private val polygon: Polygon
) : PolyPointAnnotationOptions(polygon) {

    var fillColor: String = ""
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    override fun getProperties(mapController: MapController): Array<String?> {
        val properties = HashMap<String, String>()

        properties["id"] = polygon.getIdForMap(mapController).toString()
        if (fillColor.isNotBlank()) properties["polygon_color"] = fillColor
        if (strokeColor.isNotBlank()) properties["line_color"] = strokeColor
        if (strokeWidth != Int.MIN_VALUE) properties["line_width"] = strokeWidth.toString()
        if (strokeOutlineColor.isNotBlank()) properties["line_stroke_color"] = strokeOutlineColor
        if (strokeOutlineWidth != Int.MIN_VALUE) properties["line_stroke_width"] =
                strokeOutlineWidth.toString()
        properties["line_cap"] = lineCapType.getValue()
        properties["line_join"] = lineJoinType.getValue()

        return getStringMapAsArray(properties)
    }

}