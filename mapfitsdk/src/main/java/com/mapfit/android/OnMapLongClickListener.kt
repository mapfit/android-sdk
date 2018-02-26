package com.mapfit.android

import com.mapfit.android.geometry.LatLng

/**
 * Interface to listen map long click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapLongClickListener {

    fun onMapLongClicked(latLng: LatLng)

}