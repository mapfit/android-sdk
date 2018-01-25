package com.mapfit.mapfitsdk.geometry

import android.util.Log

/**
 * Extension functions for geometries.
 *
 * Created by dogangulcan on 12/27/17.
 */

fun LatLng.isValid(): Boolean {

//    ^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?),\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$
    val latRegex = """^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)$""".toRegex()
    val lonRegex = """^\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)${'$'}""".toRegex()

    val isLatValid = latRegex.containsMatchIn(lat.toString())
    val isLonValid = lonRegex.containsMatchIn(lon.toString())

    if (!isLatValid) {
        Log.e("Mapfit", "Invalid latitude: $lat. Should be in the range [-90, 90].")
    }

    if (!isLatValid) {
        Log.e("Mapfit", "Invalid longitude: $lon. Should be in the range [-180, 180).")
    }

    return isLatValid && isLonValid
}

fun LatLng.isEmpty(): Boolean = lat == 0.0 && lon == 0.0
