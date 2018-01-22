package com.mapfit.mapfitsdk.annotations.callback

import com.mapfit.mapfitsdk.annotations.Marker

/**
 * Created by dogangulcan on 1/19/18.
 */
interface OnMarkerAddedCallback {

    fun onMarkerAdded(marker: Marker)

    fun onError(exception: Exception)

}