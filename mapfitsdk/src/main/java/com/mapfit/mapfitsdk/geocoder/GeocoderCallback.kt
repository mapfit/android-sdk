package com.mapfit.mapfitsdk.geocoder

import com.mapfit.mapfitsdk.geocoder.model.Address

/**
 * Callback used to get responses and errors from [GeocoderApi].
 *
 * Created by dogangulcan on 1/18/18.
 */
interface GeocoderCallback {

    fun onSuccess(addressList: List<Address>)

    fun onError(message: String, e: Exception)

}