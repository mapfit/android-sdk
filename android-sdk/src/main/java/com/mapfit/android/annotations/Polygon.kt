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
    mapController: MapController,
    internal val polygon: MutableList<List<LatLng>>
) : Annotation(polygonId, mapController) {

    val points = polygon.toMutableList()
    val polygonOptions = PolygonOptions(this)
    internal lateinit var coordinates: DoubleArray
    internal lateinit var rings: IntArray

    init {
        initAnnotation(mapController, polygonId)
        parseRings(polygon)
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

    override fun getLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        polygon.forEach { it.forEach { builder.include(it) } }
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

}