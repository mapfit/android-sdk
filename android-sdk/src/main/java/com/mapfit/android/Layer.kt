package com.mapfit.android

import com.mapfit.android.annotations.Annotation
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.Polygon
import com.mapfit.android.annotations.Polyline
import com.mapfit.android.geometry.LatLngBounds

/**
 * Layer structure is to re-use [Annotation]s and have them synchronised across maps.
 *
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    val annotations = mutableListOf<Annotation>()

    private val maps = mutableListOf<MapController>()


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

        maps.forEach { mapController ->
            annotation.addToMap(mapController)
        }
    }

    internal fun addMap(mapController: MapController) {
        maps.takeIf { !maps.contains(mapController) }?.add(mapController)
        annotations.forEach {
            it.addToMap(mapController)
        }
    }

    /**
     * Removes the input annotation(s) from the layer.
     */
    fun remove(vararg annotation: Annotation) {
        annotation.forEach {
            it.remove(maps)
            it.subAnnotation?.remove(maps)
            annotations.remove(it)
            it.layers.remove(this@Layer)
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