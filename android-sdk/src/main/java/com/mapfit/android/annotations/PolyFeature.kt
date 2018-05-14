package com.mapfit.android.annotations

/**
 * Defines characteristics for poly features such as [Polyline] or [Polygon].
 */
internal interface PolyFeature {

    fun getProperties(idForMap: String): Array<String?>

    fun getStringMapAsArray(properties: Map<String, String>): Array<String?> {
        val out = arrayOfNulls<String>(properties.size * 2)
        var i = 0
        for ((key, value) in properties) {
            out[i++] = key
            out[i++] = value
        }
        return out
    }

}