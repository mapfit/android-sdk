package com.mapfit.android.annotations

class PolylineOptions internal constructor(
    private val polyline: Polyline
) : PolyPointAnnotationOptions(polyline) {

    override fun getProperties(): Array<String?> {
        val properties = HashMap<String, String>()

        properties["id"] = "$polyline.id"

        if (strokeColor.isNotBlank()) properties["line_color"] = strokeColor
        if (strokeWidth != Int.MIN_VALUE) properties["line_width"] = "$strokeWidth"
        if (strokeOutlineColor.isNotBlank()) properties["line_stroke_color"] = strokeOutlineColor
        if (strokeOutlineWidth != Int.MIN_VALUE) properties["line_stroke_width"] =
                "$strokeOutlineWidth"
        properties["line_cap"] = lineCapType.getValue()
        properties["line_join"] = lineJoinType.getValue()

        return getStringMapAsArray(properties)
    }

}