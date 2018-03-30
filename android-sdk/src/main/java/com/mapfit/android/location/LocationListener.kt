package com.mapfit.android.location

import android.location.Location

/**
 * Interface for listening device location changes.
 *
 * Created by dogangulcan on 3/2/18.
 */
interface LocationListener {

    fun onLocation(location: Location)

    fun onProviderStatus(status: ProviderStatus)

}