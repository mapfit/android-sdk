package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.Polygon
import com.mapfit.mapfitsdk.annotations.Polyline
import com.mapfit.mapfitsdk.geometry.LatLngBounds

/**
 * Layer structure enables
 *
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    val annotations = mutableListOf<Annotation>()

    private val maps = mutableListOf<MapController>()

    private var drawOrder = -1

    var visibility = true
        set(value) {
            if (field != value) {
                annotations.forEach { it.visibility = value }
                field = value
            }
        }

    /**
     * Adds the input annotation to the layer.
     *
     * @param annotation e.g. marker, polyline, polygon, etc.
     */
    fun add(annotation: Annotation) {
        annotations.add(annotation)
        annotation.bindToLayer(this)
        if (drawOrder > 0) {
            if (annotation is Marker) {
                annotation.markerOptions.drawOrder = drawOrder
            }

        }
        maps.forEach { mapController ->
            annotation.addToMap(mapController)
        }
    }

    fun setDrawOrder(orderIndex: Int) {
        annotations.forEach {
            if (it is Marker) {
                it.markerOptions.drawOrder = orderIndex
            }
        }
        drawOrder = orderIndex
    }

    /**
     * Returns the draw order index for the layer.
     *
     * @return draw order of the layer. Will return -1 if not set.
     */
    fun getDrawOrder() = drawOrder

    internal fun addMap(mapController: MapController) {
        maps.takeIf { !maps.contains(mapController) }?.add(mapController)
        annotations.forEach { it.addToMap(mapController) }
    }

    /**
     * Removes the input annotation(s) from the layer.
     */
    fun remove(vararg annotation: Annotation) {
        annotation.forEach {
            it.subAnnotation?.remove(maps)
            it.remove(maps)
        }
    }

    /**
     * Returns the bounding coordinates for the layer.
     */
    fun getLatLngBounds(): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()

        annotations.forEach { it ->
            addBounds(it, boundsBuilder)
        }

        return boundsBuilder.build()
    }

    private fun addBounds(
        it: Annotation,
        boundsBuilder: LatLngBounds.Builder
    ) {
        when (it) {
            is Marker -> boundsBuilder.include(it.getPosition())
            is Polyline -> it.points.forEach { boundsBuilder.include(it) }
            is Polygon -> it.polygon.forEach { it.forEach { boundsBuilder.include(it) } }
        }

        it.subAnnotation?.let { addBounds(it, boundsBuilder) }
    }

    /**
     * Removes every annotation from the layer and the maps the layer is added to.
     */
    fun clear() {
        annotations.forEach { it.remove(maps) }
        annotations.clear()
    }

}