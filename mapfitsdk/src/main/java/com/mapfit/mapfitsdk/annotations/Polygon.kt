package com.mapfit.mapfitsdk.annotations

import android.content.Context
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.geometry.LatLng
import java.util.*


/**
 * Polygon is a closed Polyline where and point and start point is equals. Consists consecutive
 * points.
 *
 * Created by dogangulcan on 12/22/17.
 */
class Polygon(
    internal val context: Context,
    private val polygonId: Long,
    mapController: MapController,
    polygon: MutableList<List<LatLng>>
) : Annotation() {

    lateinit var coordinates: DoubleArray
    lateinit var rings: IntArray

    val properties by lazy {
        val props = HashMap<String, String>()
        props["type"] = "polygon"
        props["order"] = "1"
        props["fill"] = "blue"
        props["width"] = "2px"
        props["color"] = "yellow"

        val out = arrayOfNulls<String>(props.size * 2)
        var i = 0
        for ((key, value) in props) {
            out[i++] = key
            out[i++] = value
        }
        out
    }

    init {
        initAnnotation(mapController, polygonId)
        parseRings(polygon)
    }

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
                coordinates[j++] = point.lon
                coordinates[j++] = point.lat
            }
        }
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = polygonId
    }

    /**
     * Removes the polygon from the map(s) it is added to.
     */
    override fun remove() {
        mapBindings.forEach {
            it.key.removePolygon(it.value)
        }
        layers.forEach { it.remove(this) }
    }

    internal fun remove(mapController: MapController) {
        mapBindings[mapController]?.let { mapController.removePolyline(it) }
    }

    override fun getId() = polygonId

}

