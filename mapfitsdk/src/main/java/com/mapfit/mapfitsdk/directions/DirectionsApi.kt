package com.mapfit.mapfitsdk.directions

import com.mapfit.mapfitsdk.BuildConfig
import com.mapfit.mapfitsdk.Mapfit
import com.mapfit.mapfitsdk.directions.DirectionsApi.HttpHandler.directionsParser
import com.mapfit.mapfitsdk.directions.model.Route
import com.mapfit.mapfitsdk.geocoder.GeocoderApi.HttpHandler.httpClient
import com.mapfit.mapfitsdk.geocoder.model.EntranceType
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.isEmpty
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.io.IOException


/**
 * A wrapper for Mapfit Directions API. Used to obtain directions for an origin and destination
 * location or address.
 *
 * Created by dogangulcan on 1/18/18.
 */
class DirectionsApi {

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

        internal val directionsParser = DirectionsParser()
    }

    /**
     * Returns directions for given origin and destination. To have reliable results, you should
     * provide an origin and destination
     *
     * @param originAddress street address for the origin location
     * @param originLocation coordinates as [LatLng] for the origin location
     * @param destinationAddress street address for the destination location
     * @param destinationLocation coordinates as [LatLng] for the destination location
     * @param directionsType type for the directions. Default value is driving
     * @param callback will be called when [Route] is obtained
     */
    @JvmOverloads
    fun getDirections(
        originAddress: String = "",
        originLocation: LatLng = LatLng(),
        destinationAddress: String = "",
        destinationLocation: LatLng = LatLng(),
        directionsType: DirectionsType = DirectionsType.DRIVING,
        callback: DirectionsCallback
    ) {

        val body = createRequestBody(
            originAddress,
            originLocation,
            destinationAddress,
            destinationLocation,
            directionsType
        )

        val request = Request.Builder()
            .post(body!!)
            .url(getApiUrl())
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

                if (response != null && response.isSuccessful) {

                    async(UI) {
                        val route = bg {
                            response.body()?.string()?.let {
                                val moshi = Moshi.Builder().build()
                                val jsonAdapter = moshi.adapter<Route>(Route::class.java)

                                jsonAdapter.fromJson(it) as Route
                            }
                        }
                        route.await()?.let { callback.onSuccess(it) }
                    }
                } else {
                    val (message, exception) = directionsParser.parseError(response)
                    callback.onError(message, exception)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.let { callback.onError("An unexpected error has occurred", it) }
            }
        })
    }

    private fun createRequestBody(
        originAddress: String,
        originLocation: LatLng,
        destinationAddress: String,
        destinationLocation: LatLng,
        directionsType: DirectionsType
    ): RequestBody? {

        val srcAddress = when {
            !originAddress.isBlank() -> JSONObject()
                .put("street_address", originAddress)
                .put("entrance_type", EntranceType.ALL_PEDESTRIAN)
            else -> null
        }

        val srcLocation = when {
            !originLocation.isEmpty() -> JSONObject()
                .put("lat", originLocation.lat)
                .put("lon", originLocation.lon)
            else -> null
        }

        val destAddress = when {
            !destinationAddress.isBlank() -> JSONObject()
                .put("street_address", destinationAddress)
                .put("entrance_type", EntranceType.ALL_PEDESTRIAN)
            else -> null
        }

        val destLocation = when {
            !destinationLocation.isEmpty() -> JSONObject()
                .put("lat", destinationLocation.lat)
                .put("lon", destinationLocation.lon)
            else -> null
        }

        val requestBody = JSONObject()
        srcAddress?.let { requestBody.put("source-address", it) }
        srcLocation?.let { requestBody.put("source-location", it) }
        destAddress?.let { requestBody.put("destination-address", it) }
        destLocation?.let { requestBody.put("destination-location", it) }
        requestBody.put("type", directionsType.getName())

        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            requestBody.toString()
        )
    }


    private fun getApiUrl() = "https://api.mapfit.com/v2/directions?api_key=${Mapfit.getApiKey()}"

}