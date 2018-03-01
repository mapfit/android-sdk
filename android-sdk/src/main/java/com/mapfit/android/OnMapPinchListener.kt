package com.mapfit.android

import android.support.annotation.UiThread

/**
 * Interface to listen map pinch events.
 *
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