package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    val annotations = mutableListOf<Annotation>()

    private val bindings = HashMap<MapController, List<Annotation>>()

    var isVisible = true
        set(value) {
            if (field != value) {
                annotations.forEach { it.setVisible(value) }
                field = value
            }
        }

    fun add(annotation: Annotation) {
        annotations.add(annotation)
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