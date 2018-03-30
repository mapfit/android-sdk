package com.mapfit.android.geocoder

import com.mapfit.android.exceptions.MapfitAuthorizationException
import com.mapfit.android.geocoder.model.*
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds
import com.mapfit.android.utils.logException
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Geocoder API response parser.
 *
 * Created by dogangulcan on 1/18/18.
 */
internal class GeocodeParser internal constructor() {

    internal fun parseError(response: Response?): Pair<String, Exception> {

        val exception = when (response?.code()) {
            401,
            403 -> MapfitAuthorizationException()
            404,
            500 -> Exception("Can not reach to GeocoderAPI.")
            else -> {
                Exception("An error has occurred")
            }
        }


        return try {
            val jsonObject = JSONObject(response?.body()?.string())
            val message = jsonObject.getString("error")
            Pair(message, exception)
        } catch (e: Exception) {
            logException(e)
            Pair("An error has occurred", exception)
        }
    }

    internal fun parseGeocodeResponse(string: String): List<Address> {
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

                val building: Building = if (addressJson.has("building")) {
                    parseBuilding(addressJson.getJSONObject("building"))
                } else {
                    Building()
                }

                val viewport: LatLngBounds? = if (addressJson.has("viewport")) {
                    parseViewport(addressJson.getJSONObject("viewport"))
                } else {
                    null
                }

                val responseType: ResponseType? = if (addressJson.has("response_type")) {
                    ResponseType.values()
                        .find { status -> status.code == addressJson.getInt("response_type") }
                } else {
                    ResponseType.ERROR
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
                    viewport = viewport,
                    country = country,
                    building = building,
                    responseType = responseType,
                    streetAddress = streetAddress,
                    entrances = entrances ?: emptyList(),
                    lat = lat,
                    lng = lon
                )

                addressList.add(address)

            } catch (e: JSONException) {
                logException(e)
            }

        }

        return addressList
    }

    private fun parseViewport(jsonObject: JSONObject): LatLngBounds {
        return try {
            val swJson = jsonObject.getJSONObject("southwest")
            val neJson = jsonObject.getJSONObject("northeast")

            val sw = LatLng(swJson.getDouble("lat"), swJson.getDouble("lon"))
            val ne = LatLng(neJson.getDouble("lat"), neJson.getDouble("lon"))

            LatLngBounds(ne, sw)
        } catch (e: Exception) {
            logException(e)
            LatLngBounds()
        }
    }

    private fun parseBuilding(jsonObject: JSONObject): Building {
        val coordinates = jsonObject.getJSONArray("coordinates")
        val type = jsonObject.getSafeString("type")

        val polygon = when (type) {
            "Polygon" -> parsePolygon(coordinates)
            "MultiPolygon" -> parseMultiPolygon(coordinates)[0]
            else -> listOf()
        }

        return Building(polygon, type)
    }

    private fun parseMultiPolygon(jsonArray: JSONArray): List<List<List<LatLng>>> {
        val multiPolygon = mutableListOf<List<List<LatLng>>>()
        (0 until jsonArray.length())
            .map { jsonArray.getJSONArray(it) }
            .forEach { multiPolygon.add(parsePolygon(it)) }

        return multiPolygon
    }

    private fun parsePolygon(coordinates: JSONArray): List<List<LatLng>> {
        return (0 until coordinates.length())
            .mapNotNull { coordinates.optJSONArray(it) }
            .map { parseCoordinates(it) }
    }

    private fun parseCoordinates(jsonArray: JSONArray): List<LatLng> {
        val latLngList = mutableListOf<LatLng>()
        // extra array
        (0 until jsonArray.length())
            .map { jsonArray.getJSONArray(it) }
            .map { LatLng(it[1] as Double, it[0] as Double) }
            .forEach { latLngList.add(it) }

        return latLngList
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
            logException(e)
            Pair(0.0, 0.0)
        }

    private fun parseEntrances(jArray: JSONArray): List<Entrance> {
        val entrances = mutableListOf<Entrance>()

        for (i in 0 until jArray.length()) {
            try {
                val entranceJson = jArray.getJSONObject(i)

                val lat = entranceJson.getDouble("lat")
                val lon = entranceJson.getDouble("lon")

                val entranceType = if (entranceJson.has("entrance_type")) {
                    EntranceType.values()
                        .find { status ->
                            status.entranceType
                                .contentEquals(entranceJson.getString("entrance_type"))
                        }
                } else {
                    EntranceType.INTERPOLATED
                }


                val entrance = Entrance(lat, lon, entranceType)
                entrances.add(entrance)

            } catch (e: JSONException) {
                logException(e)
            }
        }

        return entrances
    }

}