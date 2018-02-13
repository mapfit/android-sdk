package com.mapfit.mapfitsdk.annotations.callback

import com.mapfit.mapfitsdk.annotations.Polygon

/**
 * Interface for listening [Polygon] click events.
 *
 * Created by dogangulcan on 1/4/18.
 */
interface OnPolygonClickListener {

    fun onPolygonClicked(polygon: Polygon)

}