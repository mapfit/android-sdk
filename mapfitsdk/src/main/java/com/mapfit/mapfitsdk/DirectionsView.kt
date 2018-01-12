package com.mapfit.mapfitsdk

import android.view.View
import com.mapfit.mapfitsdk.annotations.Route

/**
 * Created by dogangulcan on 1/4/18.
 */
class DirectionsView internal constructor() {

    lateinit var searchBar: View

    lateinit var bottomSheet: View

    internal var isVisible = false

    internal fun drawDirections(route: Route) {}

    internal fun clearDirections() {}

}