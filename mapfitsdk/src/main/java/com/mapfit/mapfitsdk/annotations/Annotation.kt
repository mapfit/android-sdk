package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.Layer
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.geometry.LatLngBounds
import com.mapfit.mapfitsdk.utils.generateUniqueId
import java.util.*

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

    var visibility: Boolean = true
        set(value) {
            mapBindings.forEach {
                it.key.changeAnnotationVisibility(it.value, value)
            }
            subAnnotation?.visibility = value
            field = value
        }

    var drawOder: Int = 0
        set(value) {
            mapBindings.forEach {
                it.key.setMarkerDrawOrder(it.value, value)
            }
            subAnnotation?.drawOder = value
            field = value
        }


    internal var subAnnotation: Annotation? = null

    val id: Long by lazy { generateUniqueId() }

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

    internal fun bindToLayer(layer: Layer) {
        if (!layers.contains(layer)) {
            layers.add(layer)

            subAnnotation?.bindToLayer(layer)
        }
    }

    /**
     * Do not use this method. It is for internal usage.
     */
    fun getIdForMap(mapController: MapController): Long? = mapBindings[mapController]

    /**
     * Do not use this method. It is for internal usage.
     */
    abstract fun initAnnotation(mapController: MapController, id: Long)

    abstract fun getLatLngBounds(): LatLngBounds

    abstract fun remove()

    internal abstract fun remove(mapController: MapController)

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