package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geo.LatLng

/**
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapClickListener{

    fun onMapClicked(latLng: LatLng)

}