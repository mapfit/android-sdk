package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    internal val annotations = mutableListOf<Annotation>()
    private val maps = mutableListOf<MapController>()

    val isVisible = true
//    internal var pointer: Long = 0

    fun add(annotation: Annotation) {
        annotations.add(annotation)
    }

//    private fun setVisible(boolean: Boolean) {
//        annotations.forEach { it.setVisible(boolean) }
//    }

//    internal fun getAnnotations(): List<Annotation> = annotations
//
//    internal fun bindTo(map: MapController) {
//        maps.add(map)
//    }


}