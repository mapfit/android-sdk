package com.mapfit.mapfitsdk.annotations.callback

import com.mapfit.mapfitsdk.annotations.Marker

/**
 * Callback for listening marker addition to the map with [Geocoder].
 *
 * Created by dogangulcan on 1/19/18.
 */
interface OnMarkerAddedCallback {

    fun onMarkerAdded(marker: Marker)

    fun onError(exception: Exception)

}