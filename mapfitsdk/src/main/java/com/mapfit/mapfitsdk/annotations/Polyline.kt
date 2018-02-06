package com.mapfit.mapfitsdk.annotations

import android.content.Context
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.geometry.LatLng

/**
 * Created by dogangulcan on 12/22/17.
 */
class Polyline(
    internal val context: Context,
    private val polylineId: Long,
    mapController: MapController,
    line: MutableList<LatLng>
) : Annotation() {

    var points = line
    val polylineOptions = PolylineOptions(this)

    override fun getId() = polylineId

    init {
        initAnnotation(mapController, polylineId)
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = polylineId
        polylineOptions.updateStyle()
    }

    /**
     * Removes the polyline from the map(s) it is added to.
     */
    override fun remove() {
        mapBindings.forEach {
            it.key.removeMarker(it.value)
        }
        layers.forEach { it.remove(this) }
    }

    internal fun remove(mapController: MapController) {
        mapBindings[mapController]?.let { mapController.removePolyline(it) }
    }

    /**
     * Adds [LatLng] point(s) to the polyline.
     */
    private fun addPoint(vararg line: LatLng) {
        points.addAll(line)
        mapBindings.forEach {
            TODO() //re-add
        }
    }

    fun getCoordinates(): DoubleArray {
        val coordinates = DoubleArray(points.size * 2)
        var i = 0
        for (point in points) {
            coordinates[i++] = point.lon
            coordinates[i++] = point.lat
        }
        return coordinates
    }

}


