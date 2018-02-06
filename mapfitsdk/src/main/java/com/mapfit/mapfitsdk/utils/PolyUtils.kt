package com.mapfit.mapfitsdk.utils

import com.mapfit.mapfitsdk.geometry.LatLng


/**
 * Created by dogangulcan on 2/5/18.
 */

/**
 * Decodes an encoded path string into a sequence of LatLngs.
 */
fun decodePolyline(encodedPath: String): List<LatLng> {
    val len = encodedPath.length

    // For speed we preallocate to an upper bound on the final length, then
    // truncate the array before returning.
    val path = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        result = 1
        shift = 0
        do {
            b = encodedPath[index++].toInt() - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        path.add(LatLng(lat * 1e-6, lng * 1e-6))
    }

    return path
}
