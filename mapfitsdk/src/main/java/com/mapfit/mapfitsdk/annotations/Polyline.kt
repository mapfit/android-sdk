package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.geometry.LatLng

/**
 * Created by dogangulcan on 12/22/17.
 */
class Polyline : Annotation() {
    override fun getId(): Long? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDrawOder(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    fun Polyline(polyline: List<LatLng>, properties: Map<String, String>?): ??? {
//        this.coordinates = DoubleArray(polyline.size * 2)
//        var i = 0
//        for ((lat, lon) in polyline) {
//            coordinates[i++] = lon
//            coordinates[i++] = lat
//        }
//        if (properties != null) {
//            this.properties = getStringMapAsArray(properties)
//        }
//    }
}


