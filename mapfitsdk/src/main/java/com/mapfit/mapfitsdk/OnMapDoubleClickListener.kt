package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geometry.LatLng

/**
 * Interface to listen map double click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapDoubleClickListener{

    fun onMapDoubleClicked(latLng: LatLng)

}