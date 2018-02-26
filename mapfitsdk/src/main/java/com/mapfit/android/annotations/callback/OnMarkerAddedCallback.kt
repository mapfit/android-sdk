package com.mapfit.android.annotations.callback

import com.mapfit.android.annotations.Marker

/**
 * Callback for listening marker addition to the map with [Geocoder].
 *
 * Created by dogangulcan on 1/19/18.
 */
interface OnMarkerAddedCallback {

    fun onMarkerAdded(marker: Marker)

    fun onError(exception: Exception)

}