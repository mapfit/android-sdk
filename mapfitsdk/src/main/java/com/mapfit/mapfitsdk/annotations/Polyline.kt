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
        mapController.fillPolyline(id, points)
    }

    override fun remove() {
        mapBindings.forEach {
            it.key.removeMarker(it.value)
        }
    }

    fun addPoint(vararg line: LatLng) {
        points.addAll(line)
    }

}


