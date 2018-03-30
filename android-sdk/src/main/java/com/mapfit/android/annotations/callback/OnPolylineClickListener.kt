package com.mapfit.android.annotations.callback

import com.mapfit.android.annotations.Polyline

/**
 * Interface for listening [Polyline] click events.
 *
 * Created by dogangulcan on 3/9/18.
 */
interface OnPolylineClickListener {

    fun onPolylineClicked(polyline: Polyline)

}