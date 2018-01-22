package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.util.Log
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.utils.isValid
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.launch

/**
 * Created by dogangulcan on 12/19/17.
 */
class Marker internal constructor(
        private val context: Context,
        private var markerId: Long,
        private val mapController: MapController
) : Annotation() {


    private var position: LatLng = LatLng(0.0, 0.0)

    private var isFlat: Boolean = false

    private var isVisible: Boolean = true

    private val markerOptions = MarkerOptions(markerId, mapController)

    private var data: Any? = null

    init {
        setIcon(MapfitMarker.LIGHT_DEFAULT)
    }

    fun getPosition(): LatLng = position

    fun setPosition(latLng: LatLng): Marker {
        if (latLng.isValid()) {
            val markerPositionSet = mapController.setMarkerPointEased(
                    markerId,
                    latLng.lon,
                    latLng.lat,
                    0,
                    MapController.EaseType.CUBIC
            )

            updatePosition(markerPositionSet, latLng)
        }
        return this
    }

    private fun setPositionEased(latLng: LatLng, duration: Int): Marker {
        if (latLng.isValid()) {
            val markerPositionSet = mapController.setMarkerPointEased(
                    markerId,
                    latLng.lon,
                    latLng.lat,
                    duration,
                    MapController.EaseType.CUBIC
            )

            updatePosition(markerPositionSet, latLng)
        }
        return this
    }

    private fun updatePosition(markerPositionSet: Boolean, latLng: LatLng) {
        if (markerPositionSet) {
            position = latLng
        } else {
            Log.e("Mapfit",
                    "Setting Marker position is failed for ${latLng.lat}, ${latLng.lon}")
        }
    }

    private fun setIcon(drawable: Drawable): Marker {
        val density = context.resources.displayMetrics.densityDpi
        val bitmapDrawable = drawable as BitmapDrawable
        bitmapDrawable.setTargetDensity(density)
        val bitmap = bitmapDrawable.bitmap
        bitmap.density = density
        setBitmap(bitmap)
        return this
    }

    private fun setIcon(@DrawableRes drawableId: Int): Marker {
        val options = BitmapFactory.Options()
        options.inTargetDensity = context.resources.displayMetrics.densityDpi
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId, options)
        setBitmap(bitmap)
        return this
    }

    private fun setIcon(@NonNull mapfitMarker: MapfitMarker): Marker {
        setIcon(mapfitMarker.getMarkerUrl())
        return this
    }

    /**
     * Set icon with a url consist of a image.
     */
    private fun setIcon(imageUrl: String): Marker {
        launch {
            val drawable = loadImageFromUrl(imageUrl)
            drawable.await()?.let { setIcon(it) }
        }

        return this
    }

    private fun setDrawOder(index: Int) {
        mapController.setMarkerDrawOrder(markerId, index)
    }

    override fun getId(): Long = markerId

    private fun setData(data: Any): Marker {
        this.data = data
        return this
    }

    private fun setBitmap(bitmap: Bitmap): Boolean {
        val density = context.resources.displayMetrics.densityDpi
        val width = bitmap.getScaledWidth(density)
        val height = bitmap.getScaledHeight(density)

        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)

        val abgr = IntArray(width * height)
        var row: Int
        var col: Int
        for (i in argb.indices) {
            col = i % width
            row = i / width
            val pix = argb[i]
            val pb = pix shr 16 and 0xff
            val pr = pix shl 16 and 0x00ff0000
            val pix1 = pix and -0xff0100 or pr or pb
            val flippedIndex = (height - 1 - row) * width + col
            abgr[flippedIndex] = pix1
        }

        return mapController.setMarkerBitmap(markerId, width, height, abgr)
    }

    private fun setVisible(visible: Boolean) {
        val success = mapController.setMarkerVisible(markerId, visible)
        if (success) {
            isVisible = visible
        }
    }

    private fun getVisible(): Boolean = isVisible

    fun remove() {
        mapController.removeMarker(this)
    }
}