package com.mapfit.android.geocoder

import com.mapfit.android.BuildConfig
import com.mapfit.android.Mapfit
import com.mapfit.android.geocoder.Geocoder.HttpHandler.geocodeParser
import com.mapfit.android.geocoder.Geocoder.HttpHandler.httpClient
import com.mapfit.android.geometry.LatLng
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
class Geocoder {

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
     * Returns a list of addresses with entrance points. Optionally returns the associated building
     * polygon (where available).
     *
     * @param address an address such as "119w 24th st NY" venue names are shouldn't be given
     * @param includeBuilding flag for including building polygon
     * @param callback for response and errors
     */
    @JvmOverloads
    fun geocode(
        address: String,
        includeBuilding: Boolean = false,
        callback: GeocoderCallback
    ) {
        val request = Request.Builder()
            .url(createGeocodingURl(address, includeBuilding))
            .build()

        callApi(request, callback)
    }

    /**
     * Returns a list of addresses with entrance points. Optionally returns the associated building
     * polygon (where available).
     *
     * @param latLng coordinates for the expected geocoding
     * @param includeBuilding flag for including building polygon
     * @param callback for response and errors
     */
    @JvmOverloads
    fun reverseGeocode(
        latLng: LatLng,
        includeBuilding: Boolean = false,
        callback: GeocoderCallback
    ) {
        val request = Request.Builder()
            .url(createReverseGeocodingURl(latLng, includeBuilding))
            .build()

        callApi(request, callback)
    }

    private fun callApi(
        request: Request,
        callback: GeocoderCallback
    ) {
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


    private fun createGeocodingURl(
        address: String,
        withBuilding: Boolean
    ): String =
        "https://api.mapfit.com/v2/geocode?" +
                "street_address=$address" +
                "&building=$withBuilding" +
                "&api_key=${Mapfit.getApiKey()}"

    private fun createReverseGeocodingURl(
        latLng: LatLng,
        withBuilding: Boolean
    ): String =
        "https://api.mapfit.com/v2/reverse-geocode?" +
                "lat=${latLng.lat}&lon=${latLng.lng}" +
                "&building=$withBuilding" +
                "&api_key=${Mapfit.getApiKey()}"

}