package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.geo.LatLng

/**
 * Created by dogangulcan on 12/22/17.
 */
data class Polyline(
        var width: Int = 2,
        var color: Int = 0x111144
) : Annotation() {
    override fun getLocation(): LatLng {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getId(): Long? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val style = "\"{ style: 'lines', color: '#06a6d4', width: 5px, order: 2000 }\""

    fun setStyle(style: String): Polyline {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return this
    }

    override fun hide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun show() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setZIndex(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}