package com.mapfit.mapfitsdk.geocoder

import com.mapfit.mapfitsdk.BuildConfig
import com.mapfit.mapfitsdk.exceptions.MapfitAuthorizationException
import com.mapfit.mapfitsdk.geocoder.model.Address
import com.mapfit.mapfitsdk.geocoder.model.Entrance
import com.mapfit.mapfitsdk.geocoder.model.EntranceType
import com.mapfit.mapfitsdk.geocoder.model.LocationStatus
import com.mapfit.mapfitsdk.utils.DebugUtils
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by dogangulcan on 1/18/18.
 */
internal class GeocodeParser internal constructor() {

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

    fun parseGeocodeResponse(string: String): List<Address> {
        val jArray = JSONArray(string)

        val addressList = mutableListOf<Address>()

        for (i in 0 until jArray.length()) {
            try {

                val addressJson = jArray.getJSONObject(i)

                val locality = addressJson.getSafeString("locality")
                val postalCode = addressJson.getSafeString("postal_code")
                val country = addressJson.getSafeString("country")
                val adminArea = addressJson.getSafeString("admin_1")
                val neighborhood = addressJson.getSafeString("neighborhood")
                val streetAddress = addressJson.getSafeString("street_address")

                val statusCode: LocationStatus? = if (addressJson.has("response_type")) {
                    LocationStatus.values()
                            .find { status -> status.code == addressJson.getInt("response_type") }
                } else {
                    LocationStatus.ERROR
                }

                val entrances = if (addressJson.has("entrances")) {
                    parseEntrances(addressJson.getJSONArray("entrances"))
                } else {
                    null
                }

                val (lat, lon) = if (addressJson.has("location")) {
                    parseLocation(addressJson.getJSONObject("location"))
                } else {
                    Pair(0.0, 0.0)
                }

                val address = Address(
                        locality = locality,
                        postalCode = postalCode,
                        adminArea = adminArea,
                        neighborhood = neighborhood,
                        country = country,
                        status = statusCode,
                        streetAddress = streetAddress,
                        entrances = entrances ?: emptyList(),
                        latitude = lat,
                        longitude = lon
                )

                addressList.add(address)

            } catch (e: JSONException) {
                DebugUtils.logException(e)
            }

        }

        return addressList
    }

    private fun JSONObject.getSafeString(name: String): String =
            if (has(name)) {
                getString(name)
            } else {
                ""
            }

    private fun parseLocation(jsonObject: JSONObject?): Pair<Double, Double> =
            try {
                val latitude = jsonObject?.getDouble("lat")
                val longitude = jsonObject?.getDouble("lon")
                Pair(latitude ?: 0.0, longitude ?: 0.0)
            } catch (e: JSONException) {
                DebugUtils.logException(e)
                Pair(0.0, 0.0)
            }

    private fun parseEntrances(jArray: JSONArray): List<Entrance> {
        val entrances = mutableListOf<Entrance>()

        for (i in 0 until jArray.length()) {
            try {
                val entranceJson = jArray.getJSONObject(i)

                val lat = entranceJson.getDouble("lat")
                val lon = entranceJson.getDouble("lon")
                val entranceType = EntranceType.values()
                        .find { status ->
                            status.sourceName
                                    .contentEquals(entranceJson.getString("entrance_type"))
                        }

                val entrance = Entrance(lat, lon, entranceType)
                entrances.add(entrance)

            } catch (e: JSONException) {
                DebugUtils.logException(e)
            }
        }

        return entrances
    }

}