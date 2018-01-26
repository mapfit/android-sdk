package com.mapfit.mapfitsdk

import android.support.annotation.UiThread

/**
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapPinchListener {

    /**
     *  Called when the map is pinched. This can be called each frame update and should not perform
     *  heavy calculations.
     */
    @UiThread
    fun onMapPinch()

}