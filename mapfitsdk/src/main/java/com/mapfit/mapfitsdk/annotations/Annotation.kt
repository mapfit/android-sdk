package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.geo.LatLng

/**
 * Created by dogangulcan on 12/22/17.
 */
abstract class Annotation {

    abstract fun setZIndex(index: Int)

    abstract fun hide()

    abstract fun show()

    abstract fun getId(): Long?

    abstract fun getLocation(): LatLng

}