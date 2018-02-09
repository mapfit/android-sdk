package com.mapfit.mapfitsdk.annotations.callback

import com.mapfit.mapfitsdk.annotations.Polyline

/**
 * Interface for listening [Polyline] click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnPolylineClickListener {

    fun onPolylineClicked(polyline: Polyline)

}