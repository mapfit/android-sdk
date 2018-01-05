package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Annotation

/**
 * Created by dogangulcan on 12/21/17.
 */
class Layer {

    private val annotations = mutableListOf<Annotation>()

    fun add(annotation: Annotation) {
        annotations.add(annotation)
    }

    fun setVisible(boolean: Boolean) {
        annotations.forEach { it.setVisible(boolean) }
    }

    fun getAnnotations(): List<Annotation> = annotations


}