package com.mapfit.android

import com.mapfit.android.geometry.LatLng

/**
 * Interface to listen map click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapClickListener {

    fun onMapClicked(latLng: LatLng)

}