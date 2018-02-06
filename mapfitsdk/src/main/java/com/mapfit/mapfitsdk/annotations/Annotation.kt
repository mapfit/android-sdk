package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.Layer
import com.mapfit.mapfitsdk.MapController

/**
 *
 * Created by dogangulcan on 12/22/17.
 */
abstract class Annotation {

    internal val mapBindings = HashMap<MapController, Long>()
    internal val layers = mutableListOf<Layer>()
    private var isVisible: Boolean = true

    internal fun addToMap(mapController: MapController) {
        if (!mapBindings.containsKey(mapController)) {
            val id = mapController.addAnnotation(this)
            mapBindings[mapController] = id
            initAnnotation(mapController, id)
        }
    }

    fun bindToLayer(layer: Layer) {
        if (!layers.contains(layer)) layers.add(layer)
    }

    fun setDrawOrder(drawIndex: Int) =
        mapBindings.forEach {
            it.key.setMarkerDrawOrder(it.value, drawIndex)
        }

    fun setVisible(visible: Boolean) {
        mapBindings.forEach {
            it.key.setMarkerVisible(it.value, visible)
        }
        isVisible = visible
    }

    fun getVisible() = isVisible

    abstract fun getId(): Long

    abstract fun initAnnotation(mapController: MapController, id: Long)

    abstract fun remove()

    internal fun remove(maps: MutableList<MapController>) {
        mapBindings.filter { maps.contains(it.key) }
            .forEach { it.key.removeMarker(it.value) }
    }


}