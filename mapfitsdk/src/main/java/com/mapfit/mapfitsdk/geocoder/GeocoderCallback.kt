package com.mapfit.mapfitsdk.geocoder

import com.mapfit.mapfitsdk.geocoder.model.Address

/**
 * Created by dogangulcan on 1/18/18.
 */
interface GeocoderCallback {

    fun onResponse(addressList: List<Address>)

//    fun onError()
}