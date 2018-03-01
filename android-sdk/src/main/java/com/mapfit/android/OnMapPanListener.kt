package com.mapfit.android

import android.support.annotation.UiThread

/**
 * Interface to listen map pan events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapPanListener {

    /**
     *  Called when the map is panned. This can be called each frame update and should not perform
     *  heavy calculations.
     */
    @UiThread
    fun onMapPan()

}