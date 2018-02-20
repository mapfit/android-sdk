package com.mapfit.mapfitsdk.directions

import com.mapfit.mapfitsdk.directions.model.Route

/**
 * Callback to listen [Directions] result.
 *
 * Created by dogangulcan on 2/4/18.
 */
interface DirectionsCallback {

    fun onSuccess(route: Route)

    fun onError(message: String, e: Exception)

}