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

    fun parseError(response: Response?): Pair<String, Exception> {

        return when (response?.code()) {
            403 -> Pair("Mapfit Authorization Exception", MapfitAuthorizationException())
            400 -> parseResponseForError(response)
            else -> getDefaultException()
        }
    }

    private fun parseResponseForError(response: Response): Pair<String, Exception> {
        val errorJson = JSONObject(response.body()?.string())
        return if (errorJson.has("response_type")) {
            when (errorJson.getInt("response_type")) {
                0 -> Pair(
                    "An origin and destination address or location must be specified.",
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

    private fun getDefaultException() = Pair("An unexpected error has occurred", Exception())

}