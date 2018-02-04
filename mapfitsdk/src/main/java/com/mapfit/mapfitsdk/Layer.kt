package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    val annotations = mutableListOf<Annotation>()

    private val maps = mutableListOf<MapController>()

    var isVisible = true
        set(value) {
            if (field != value) {
                annotations.forEach { it.setVisible(value) }
                field = value
            }
        }

    /**
     * Adding the given annotation to te layer. The annotation will be added to the maps that
     * has the layer.
     */
    fun add(annotation: Annotation) {
        annotations.add(annotation)

        maps.forEach { mapController ->
            annotation.addToMap(mapController)
        }
    }


    internal fun addMap(mapController: MapController) {
        maps.takeIf { !maps.contains(mapController) }?.add(mapController)
        annotations.forEach { it.addToMap(mapController) }
    }


    fun remove(vararg annotation: Annotation) {
        annotation.forEach {
            it.remove()
            annotations.remove(it)
        }
    }

    fun clear() {
        annotations.forEach { it.remove() }
    }


}