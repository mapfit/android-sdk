package com.mapfit.android.annotations.callback

import com.mapfit.android.annotations.Marker

/**
 * Interface for listening [Marker] click events.
 *
 * Created by dogangulcan on 12/28/17.
 */
interface OnMarkerClickListener {

    fun onMarkerClicked(marker: Marker)

}