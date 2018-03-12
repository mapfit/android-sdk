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
    mapController: MapController,
    private val line: MutableList<LatLng>
) : Annotation(polylineId, mapController) {

    val points = line.toMutableList()
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
            it.key.addPolyline(points)
            it.key.removePolyline(it.value)
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
        line.forEach { builder.include(it) }
        return builder.build()
    }

}


