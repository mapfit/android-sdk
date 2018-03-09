package com.mapfit.android.annotations.callback

import com.mapfit.android.annotations.Polygon

/**
 * Interface for listening [Polygon] click events.
 *
 * Created by dogangulcan on 3/9/18.
 */
interface OnPolygonClickListener {

    fun onPolygonClicked(polygon: Polygon)

}