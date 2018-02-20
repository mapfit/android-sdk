package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geometry.LatLng

/**
 * Interface to listen map long click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapLongClickListener {

    fun onMapLongClicked(latLng: LatLng)

}