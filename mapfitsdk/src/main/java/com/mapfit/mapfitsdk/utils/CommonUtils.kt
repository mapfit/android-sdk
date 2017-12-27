package com.mapfit.mapfitsdk.utils

import android.graphics.drawable.Drawable
import android.util.Log
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.net.URL

/**
 * Created by dogangulcan on 12/21/17.
 */

private val MIN_ZOOM_LEVEL = 1f
private val MAX_ZOOM_LEVEL = 20.5f

fun isValidZoomLevel(zoomLevel: Float): Boolean {
    return if (zoomLevel !in MIN_ZOOM_LEVEL..MAX_ZOOM_LEVEL) {
        Log.w("MapView", "Zoom level must be between ${MIN_ZOOM_LEVEL} and ${MAX_ZOOM_LEVEL}")
        false
    } else {
        true
    }
}

fun loadImageFromUrl(url: String): Deferred<Drawable?> = async {
    try {
        val inputStream = URL(url).openStream()
        val drawable = Drawable.createFromStream(inputStream, "")
        drawable
    } catch (e: Exception) {
        //todo log
        e.printStackTrace()
        null
    }


}
