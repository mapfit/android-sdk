package com.mapfit.mapfitsdk.directions

import com.mapfit.mapfitsdk.BuildConfig
import com.mapfit.mapfitsdk.Mapfit
import com.mapfit.mapfitsdk.directions.DirectionsApi.HttpHandler.directionsParser
import com.mapfit.mapfitsdk.directions.model.Route
import com.mapfit.mapfitsdk.geocoder.GeocoderApi.HttpHandler.httpClient
import com.mapfit.mapfitsdk.geocoder.model.EntranceType
import com.mapfit.mapfitsdk.geometry.LatLng
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 * A class for obtaining directions for an origin and destination points.
 *
 * Created by dogangulcan on 1/18/18.
 */
class DirectionsApi {

    enum class TYPE(private val type: String) {
        DRIVING("driving"),
        WALKING("walking"),
        CYCLING("cycling");

        fun getName(): String {
            return type
        }
    }

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

    @JvmOverloads
    fun getDirections(
        originAddress: String = "",
        originLocation: LatLng = LatLng(),
        destinationAddress: String = "",
        destinationLocation: LatLng = LatLng(),
        directionsType: TYPE = TYPE.DRIVING,
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
        directionsType: TYPE
    ): RequestBody? {
        val sourceBody = JSONObject()
        try {
            sourceBody.put("street_address", originAddress)
            sourceBody.put("lat", originLocation.lat)
            sourceBody.put("lon", originLocation.lon)
        } catch (ignored: JSONException) {
        }

        val destinationBody = JSONObject()
        try {
            destinationBody.put("street_address", destinationAddress)
            destinationBody.put("lat", destinationLocation.lat)
            destinationBody.put("lon", destinationLocation.lon)
            destinationBody.put("entrance_type", EntranceType.ALL_PEDESTRIAN)
        } catch (ignored: JSONException) {
        }

        val requestBody = JSONObject()
        requestBody.put("source-address", sourceBody)
        requestBody.put("destination-address", destinationBody)
        requestBody.put("type", directionsType.getName())

        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            requestBody.toString()
        )
        return body
    }


    private fun getApiUrl() = "https://api.mapfit.com/v2/directions?api_key=${Mapfit.getApiKey()}"

}