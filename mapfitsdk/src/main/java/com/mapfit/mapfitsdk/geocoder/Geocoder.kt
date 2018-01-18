package com.mapfit.mapfitsdk.geocoder

import android.net.Uri
import com.mapfit.mapfitsdk.Mapfit
import com.mapfit.mapfitsdk.geocoder.Geocoder.HttpHandler.httpClient
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.IOException


/**
 * Created by dogangulcan on 1/18/18.
 */
class Geocoder {

    object HttpHandler {
        val httpClient = OkHttpClient()
    }

    fun geocodeAddress(address: String, callback: GeocoderCallback) {
        val request = Request.Builder()
                .url(createRequestUrl(address))
                .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
//                async(UI) {
                val addressList =
//                            bg {
                        response?.body()?.string()?.let {
                            GeocodeParser().parseGeocodeResponse(it)
//                        }
                        }
                addressList?.let { callback.onResponse(it) }
//            }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

    private fun createRequestUrl(address: String): String {
        val builder = Uri.Builder()

        builder.scheme("https")
                .authority("api.mapfit.com")
                .appendPath("v2")
                .appendPath("geocode")
                .appendQueryParameter("street_address", address)
                .appendQueryParameter("building", "false")
                .appendQueryParameter("api_key", Mapfit.getApiKey())

        return builder.build().toString()
    }
}