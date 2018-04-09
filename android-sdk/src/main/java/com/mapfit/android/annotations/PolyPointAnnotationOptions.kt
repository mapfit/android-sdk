package com.mapfit.android.annotations

import kotlinx.coroutines.experimental.Job

abstract class PolyPointAnnotationOptions(
    private var annotation: Annotation
) {

    private var styleUpdateJob = Job()

    var strokeWidth: Int = Integer.MIN_VALUE
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    var strokeColor: String = ""
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    var strokeOutlineColor: String = ""
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    var strokeOutlineWidth: Int = Integer.MIN_VALUE
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    var lineCapType: CapType = CapType.MITER
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }

    var lineJoinType: JoinType = JoinType.MITER
        set(value) {
            if (field != value) {
                field = value
                updateStyle()
            }
        }


    internal fun updateStyle() {
        for ((mapController, _) in annotation.mapBindings) {
            mapController.removeAnnotation(annotation)
            mapController.addAnnotation(annotation)
        }
    }

    internal abstract fun getProperties(): Array<String?>

    internal fun getStringMapAsArray(properties: Map<String, String>): Array<String?> {
        val out = arrayOfNulls<String>(properties.size * 2)
        var i = 0
        for ((key, value) in properties) {
            out[i++] = key
            out[i++] = value
        }
        return out
    }

}