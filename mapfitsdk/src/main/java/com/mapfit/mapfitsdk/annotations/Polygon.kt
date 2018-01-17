package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.annotations.base.AnnotationStyle

/**
 * Created by dogangulcan on 12/22/17.
 */
class Polygon internal constructor(private val tgMarker: Marker) : Annotation() {
    override fun getId(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    init {
//        tgMarker.markerOptions.style = (AnnotationStyle.POLYGON.style)
    }


//
//    fun Polygon(polygon: List<List<LatLng>>, properties: Map<String, String>?): ??? {
//        this.rings = IntArray(polygon.size)
//        var i = 0
//        var n_points = 0
//        for (ring in polygon) {
//            n_points += ring.size
//            rings[i++] = ring.size
//        }
//        this.coordinates = DoubleArray(2 * n_points)
//        var j = 0
//        for (ring in polygon) {
//            for ((lat, lon) in ring) {
//                coordinates[j++] = lon
//                coordinates[j++] = lat
//            }
//        }
//        if (properties != null) {
//            this.properties = getStringMapAsArray(properties)
//        }
//    }
}