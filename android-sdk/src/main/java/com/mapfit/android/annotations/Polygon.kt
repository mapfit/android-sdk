package com.mapfit.android.annotations

import android.content.Context
import com.mapfit.android.MapController
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds


/**
 * Polygon is a closed Polyline where and point and start point is equals. Consists consecutive
 * points.
 *
 * Created by dogangulcan on 12/22/17.
 */
class Polygon(
    internal val context: Context,
    polygonId: Long,
    polygonOptions: PolygonOptions,
    mapController: MapController
) : Annotation(polygonId, mapController), PolyFeature {

    val points = polygonOptions.points.toMutableList()

    var fillColor = polygonOptions.fillColor
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var strokeWidth = polygonOptions.strokeWidth
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var strokeColor = polygonOptions.strokeColor
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var strokeOutlineColor = polygonOptions.strokeOutlineColor
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var strokeOutlineWidth = polygonOptions.strokeOutlineWidth
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var lineJoinType = polygonOptions.lineJoinType
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    var drawOrder = polygonOptions.drawOrder
        set(value) {
            if (field != value) {
                field = value
                refreshPolygon()
            }
        }

    internal lateinit var coordinates: DoubleArray
    internal lateinit var rings: IntArray

    init {
        tag = polygonOptions.tag
        parseRings(points)
        initAnnotation(mapController, polygonId)
    }

    /**
     * Parses the given [LatLng] points to rings.
     */
    private fun parseRings(polygon: MutableList<List<LatLng>>) {
        rings = IntArray(polygon.size)

        var totalPoints = 0
        for ((i, ring) in polygon.withIndex()) {
            totalPoints += ring.size
            rings[i] = ring.size
        }
        this.coordinates = DoubleArray(2 * totalPoints)

        var j = 0
        for (ring in polygon) {
            for (point in ring) {
                coordinates[j++] = point.lng
                coordinates[j++] = point.lat
            }
        }
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = id
    }

    private fun refreshPolygon() {
        mapBindings.forEach {
            it.key.removePolygon(it.value)

            it.key.addPolygon(
                PolygonOptions()
                    .points(points)
                    .fillColor(fillColor)
                    .strokeWidth(strokeWidth)
                    .strokeColor(strokeColor)
                    .drawOrder(drawOrder)
                    .strokeOutlineColor(strokeOutlineColor)
                    .strokeOutlineWidth(strokeOutlineWidth)
                    .lineJoinType(lineJoinType)
            )

        }
    }

    override fun getLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        points.forEach { it.forEach { builder.include(it) } }
        return builder.build()
    }

    /**
     * Removes the polygon from the map(s) it is added to.
     */
    override fun remove() {
        mapBindings.forEach {
            it.key.removePolygon(it.value)
        }
        mapBindings.clear()

        layers.forEach { it.remove(this) }
    }

    override fun remove(mapController: MapController) {
        mapBindings[mapController]?.let {
            mapController.removePolygon(it)
        }
        mapBindings.remove(mapController)
    }

    override fun getProperties(idForMap: String): Array<String?> {
        val properties = HashMap<String, String>()

        properties["id"] = idForMap
        if (fillColor.isNotBlank()) properties["polygon_color"] = fillColor
        if (drawOrder != Int.MIN_VALUE) {
            properties["polygon_order"] = "$drawOrder"
            properties["line_order"] = (drawOrder - 1).toString()
        }
        if (strokeColor.isNotBlank()) properties["line_color"] = strokeColor
        if (strokeWidth != Int.MIN_VALUE) properties["line_width"] = strokeWidth.toString()
        if (strokeOutlineColor.isNotBlank()) properties["line_stroke_color"] = strokeOutlineColor
        if (strokeOutlineWidth != Int.MIN_VALUE) properties["line_stroke_width"] =
                strokeOutlineWidth.toString()
        properties["line_join"] = lineJoinType.getValue()

        return getStringMapAsArray(properties)
    }

}