package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.Layer
import com.mapfit.mapfitsdk.MapController

/**
 *
 * Created by dogangulcan on 12/22/17.
 */
abstract class Annotation {

    internal val mapBindings = HashMap<MapController, Long>()

    internal fun addToMap(mapController: MapController) {
        if (!mapBindings.containsKey(mapController)) {
            val id = mapController.addAnnotation(this)
            mapBindings[mapController] = id
            initAnnotation(mapController, id)
        }
    }

    fun bindToLayer(layer: Layer) {

    }

    fun boundTo(mapController: MapController) = mapBindings.containsKey(mapController)

    fun setDrawOrder(drawIndex: Int) =
        mapBindings.forEach {
            it.key.setMarkerDrawOrder(it.value, drawIndex)
        }

    abstract fun setVisible(visible: Boolean)

    abstract fun getId(): Long

    abstract fun initAnnotation(mapController: MapController, id: Long)

    abstract fun remove()

}