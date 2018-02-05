package com.mapfit.mapfitsdk.directions

import com.mapfit.mapfitsdk.exceptions.MapfitAuthorizationException
import com.mapfit.mapfitsdk.utils.DebugUtils
import okhttp3.Response
import org.json.JSONObject

/**
 * Created by dogangulcan on 2/4/18.
 */
class DirectionsParser {

    //    fun parseDirectionsResponse(it: String): Route {
//
//
//    }
//
    fun parseError(response: Response?): Pair<String, Exception> {

        val exception = when (response?.code()) {
            403 -> MapfitAuthorizationException()
            else -> {
                Exception("An error has occurred")
            }
        }

        val jsonObject = JSONObject(response?.body()?.string())

        return try {
            val message = jsonObject.getString("error")
            Pair(message, exception)
        } catch (e: Exception) {
            DebugUtils.logException(e)
            Pair("An error has occurred", exception)
        }
    }

}