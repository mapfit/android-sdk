package com.mapfit.android.annotations

import android.content.Context
import com.mapfit.android.MapController
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds

/**
 * Polyline represent a list of consecutive points that can be drawn as a line.
 *
 * Created by dogangulcan on 12/22/17.
 */
class Polyline(
    internal val context: Context,
    private val polylineId: Long,
    polylineOptions: PolylineOptions,
    mapController: MapController
) : Annotation(polylineId, mapController), PolyFeature {

    val points = polylineOptions.points.toMutableList()

    var strokeWidth = polylineOptions.strokeWidth
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var strokeColor = polylineOptions.strokeColor
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var strokeOutlineColor = polylineOptions.strokeOutlineColor
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var strokeOutlineWidth = polylineOptions.strokeOutlineWidth
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var lineCapType = polylineOptions.lineCapType
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var lineJoinType = polylineOptions.lineJoinType
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    var drawOrder = polylineOptions.drawOrder
        set(value) {
            if (field != value) {
                field = value
                refreshPolyline()
            }
        }

    internal val coordinates by lazy {
        val coordinates = DoubleArray(points.size * 2)
        var i = 0
        for (point in points) {
            coordinates[i++] = point.lng
            coordinates[i++] = point.lat
        }
        coordinates
    }

    init {
        tag = polylineOptions.tag
        initAnnotation(mapController, polylineId)
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = polylineId
    }

    fun addPoints(vararg latLngs: LatLng) {
        points.addAll(latLngs)
        refreshPolyline()
    }

    private fun refreshPolyline() {
        mapBindings.forEach {
            it.key.removePolyline(it.value)

            it.key.addPolyline(
                PolylineOptions()
                    .points(points)
                    .strokeWidth(strokeWidth)
                    .strokeColor(strokeColor)
                    .drawOrder(drawOrder)
                    .strokeOutlineColor(strokeOutlineColor)
                    .strokeOutlineWidth(strokeOutlineWidth)
                    .lineCapType(lineCapType)
                    .lineJoinType(lineJoinType)
            )
        }
    }

    /**
     * Removes the polyline from the map(s) it is added to.
     */
    override fun remove() {
        mapBindings.forEach {
            it.key.removePolyline(it.value)
        }

        mapBindings.clear()

        layers.forEach { it.remove(this) }
    }

    override fun remove(mapController: MapController) {
        mapBindings[mapController]?.let {
            mapController.removePolyline(it)
        }
        mapBindings.remove(mapController)
    }

    override fun getLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        points.forEach { builder.include(it) }
        return builder.build()
    }

    override fun getProperties(idForMap: String): Array<String?> {
        val properties = HashMap<String, String>()

        properties["id"] = idForMap
        if (drawOrder != Int.MIN_VALUE) {
            properties["polygon_order"] = "$drawOrder"
            properties["line_order"] = (drawOrder - 1).toString()
        }
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


