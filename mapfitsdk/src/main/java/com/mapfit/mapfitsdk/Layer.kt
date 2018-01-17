package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    private val annotations = mutableListOf<Annotation>()
    private val maps = mutableListOf<MapController>()

    val isVisible = true
    internal var pointer: Long = 0

    internal fun add(annotation: Annotation) {
        annotations.add(annotation)
    }

    internal fun setVisible(boolean: Boolean) {
        annotations.forEach { it.setVisible(boolean) }
    }

    internal fun getAnnotations(): List<Annotation> = annotations

    internal fun bindTo(map: MapController) {
        maps.add(map)
    }


}