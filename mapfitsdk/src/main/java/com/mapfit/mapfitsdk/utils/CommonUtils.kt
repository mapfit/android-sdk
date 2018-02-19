package com.mapfit.mapfitsdk.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MAX_ZOOM
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MIN_ZOOM
import com.mapfit.mapfitsdk.Mapfit
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.math.BigInteger
import java.net.URL
import java.nio.ByteBuffer
import java.util.*


/**
 * Created by dogangulcan on 12/21/17.
 */


fun isValidZoomLevel(zoomLevel: Float): Boolean {
    return if (zoomLevel !in MAP_MIN_ZOOM..MAP_MAX_ZOOM) {
        Log.w("Mapfit", "Zoom level must be between $MAP_MIN_ZOOM and $MAP_MAX_ZOOM")
        false
    } else {
        true
    }
}

fun loadImageFromUrl(url: String): Deferred<Drawable?> = async {
    if (isValidImageUrl(url) && isNetworkAvailable()) {
        try {
            val inputStream = URL(url).openStream()
            val drawable = Drawable.createFromStream(inputStream, "")

            drawable
        } catch (e: Exception) {
            DebugUtils.logException(e)
            null
        }
    } else {
        Log.w("Mapfit", "Invalid image url $url")
        null
    }
}

fun isValidImageUrl(url: String): Boolean {
    val imgRg = """(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\.(?:jpg|gif|png))""".toRegex()
    return imgRg.containsMatchIn(url)
}

private fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        Mapfit.getContext()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

internal fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(context, drawableId)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable!!).mutate()
    }

    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

internal val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

internal val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

internal fun generateUniqueId(): Long {
    var id: Long
    do {
        val uid = UUID.randomUUID()
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uid.leastSignificantBits)
        buffer.putLong(uid.mostSignificantBits)
        id = BigInteger(buffer.array()).toLong()
    } while (id < 0)

    return id
}