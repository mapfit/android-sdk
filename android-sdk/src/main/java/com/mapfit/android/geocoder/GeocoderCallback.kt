package com.mapfit.android.geocoder

import com.mapfit.android.geocoder.model.Address

/**
 * Callback used to get responses and errors from [Geocoder].
 *
 * Created by dogangulcan on 1/18/18.
 */
interface GeocoderCallback {

    fun onSuccess(addressList: List<Address>)

    fun onError(message: String, e: Exception)

}