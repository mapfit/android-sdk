package com.mapfit.mapfitsdk.geocoder

import com.mapfit.mapfitsdk.BuildConfig
import com.mapfit.mapfitsdk.Mapfit
import com.mapfit.mapfitsdk.geocoder.GeocoderApi.HttpHandler.geocodeParser
import com.mapfit.mapfitsdk.geocoder.GeocoderApi.HttpHandler.httpClient
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.IOException


/**
 * A class for handling geocoding. Geocoding is used to get coordinates for a given address.
 * Mapfit geocoder is also capable of returning entrances if the given address is belongs to a
 * building.
 *
 * Created by dogangulcan on 1/18/18.
 */
class GeocoderApi {

    object HttpHandler {
        private val logging = HttpLoggingInterceptor()

        init {
            if (BuildConfig.DEBUG_MODE) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            }
        }

        internal val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        internal val geocodeParser = GeocodeParser()
    }

    /**
     * Returns a list of addresses with entrance points.
     *
     * @param address an address such as "119w 24th st NY" venue names are shouldn't be given
     * @param callback for response and errors
     */
    fun geocodeAddress(address: String, callback: GeocoderCallback) {
        val request = Request.Builder()
            .url(createRequestUrl(address))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

                if (response != null && response.isSuccessful) {

                    async(UI) {
                        val addressList = bg {
                            response.body()?.string()?.let {
                                geocodeParser.parseGeocodeResponse(it)
                            }
                        }
                        addressList.await()?.let { callback.onSuccess(it) }
                    }
                } else {
                    val (message, exception) = geocodeParser.parseError(response)
                    callback.onError(message, exception)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.let { callback.onError("An unexpected error has occurred", it) }
            }
        })
    }

    private fun createRequestUrl(address: String): String =
        "https://api.mapfit.com/v2/geocode?" +
                "street_address=$address" +
                "&building=false" +
                "&api_key=${Mapfit.getApiKey()}"

}