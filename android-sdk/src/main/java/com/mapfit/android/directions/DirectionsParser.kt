package com.mapfit.android.directions

import com.mapfit.android.exceptions.MapfitAuthorizationException
import okhttp3.Response
import org.json.JSONObject
import java.security.InvalidParameterException

/**
 * Internally used.
 *
 * Created by dogangulcan on 2/4/18.
 */
internal class DirectionsParser {

    fun parseError(response: Response?, rawResponseJson: JSONObject): Pair<String, Exception> {

        return when (response?.code()) {
            403 -> Pair("Mapfit Authorization Exception", MapfitAuthorizationException())
            400 -> parseResponseForError(rawResponseJson)
            else -> getDefaultException()
        }
    }

    private fun parseResponseForError(errorJson: JSONObject): Pair<String, Exception> {

        return if (errorJson.has("response_type")) {
            when (errorJson.getInt("response_type")) {
                0 -> Pair(
                    "Unable to retrieve directions. Check if you have set an origin and destination address or location properly.",
                    InvalidParameterException()
                )
                else -> {
                    getDefaultException()
                }
            }
        } else {
            getDefaultException()
        }
    }

    private fun getDefaultException() =
        Pair(
            "Unable to retrieve directions due to an unexpected error.",
            Exception("Unable to retrieve directions due to an unexpected error")
        )

}