package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.MapView
import com.mapfit.mapfitsdk.MapfitMap

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer(id: String) {

    private val annotations = mutableListOf<Annotation>()

    fun add(annotation: Annotation) {
        annotations.add(annotation)
    }

    fun hide() {
        annotations.forEach { it.hide() }
    }

    fun show() {
        annotations.forEach { it.show() }
    }

    fun bindToMap(mapView: MapView) {
    }

    fun getAnnotations(): List<Annotation> = annotations
}