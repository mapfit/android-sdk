package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.annotations.base.AnnotationStyle
import com.mapzen.tangram.Marker

/**
 * Created by dogangulcan on 12/22/17.
 */
class Polygon internal constructor(private val tgMarker: Marker) : Annotation() {
    override fun getId(): Long? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    init {
        tgMarker.setStylingFromString(AnnotationStyle.POLYGON.style)
    }

    override fun setDrawOder(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVisible(visible: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}