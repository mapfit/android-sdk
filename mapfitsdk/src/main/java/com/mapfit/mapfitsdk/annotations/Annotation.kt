package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.Layer
import com.mapfit.mapfitsdk.MapController

/**
 * Base class for [Marker], [Polyline] and [Polygon].
 *
 * Created by dogangulcan on 12/22/17.
 */
abstract class Annotation(
    id: Long,
    mapController: MapController
) {

    internal val mapBindings = HashMap<MapController, Long>()
    internal val layers = mutableListOf<Layer>()
    private var isVisible: Boolean = true

    internal var subAnnotation: Annotation? = null

    init {
        mapBindings[mapController] = id
    }

    internal fun addToMap(mapController: MapController) {
        if (!mapBindings.containsKey(mapController)) {
            val newId = mapController.addAnnotation(this)
            mapBindings[mapController] = newId
            initAnnotation(mapController, newId)

            subAnnotation?.addToMap(mapController)
        }
    }

    fun bindToLayer(layer: Layer) {
        if (!layers.contains(layer)) {
            layers.add(layer)

            subAnnotation?.bindToLayer(layer)
        }
    }

    fun setDrawOrder(drawIndex: Int) {
        mapBindings.forEach {
            it.key.setMarkerDrawOrder(it.value, drawIndex)
        }
        subAnnotation?.setDrawOrder(drawIndex)
    }

    fun setVisible(visible: Boolean) {
        mapBindings.forEach {
            it.key.setMarkerVisible(it.value, visible)
        }

        subAnnotation?.setVisible(visible)

        isVisible = visible
    }

    fun getVisible() = isVisible

    fun getId(mapController: MapController): Long? = mapBindings[mapController]

    abstract fun initAnnotation(mapController: MapController, id: Long)

    abstract fun remove()

    internal fun remove(maps: List<MapController>) {
        mapBindings.filter { maps.contains(it.key) }
            .forEach {
                when (this@Annotation) {
                    is Marker -> it.key.removeMarker(it.value)
                    is Polygon -> it.key.removePolygon(it.value)
                    is Polyline -> it.key.removePolyline(it.value)
                }

            }
    }


}