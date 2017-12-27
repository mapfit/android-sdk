package com.mapfit.mapfitsdk.annotations

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import com.mapfit.mapfitsdk.annotations.base.AnnotationStyle
import com.mapfit.mapfitsdk.annotations.base.MapfitMarker
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Created by dogangulcan on 12/19/17.
 */
class Marker internal constructor(private val marker: com.mapzen.tangram.Marker? = null) : Annotation() {


    var mlocation: LatLng = LatLng(0.0, 0.0)

    init {
        setIcon(MapfitMarker.DARK_ACTIVE)
        setStyle(AnnotationStyle.POINT.style)
    }

    fun setPosition(latLng: LatLng): Marker {
        mlocation = latLng
        return this
    }

    fun setIcon(drawable: Drawable): Marker {
        marker?.setDrawable(drawable)
        return this
    }

    fun setIcon(@DrawableRes drawableId: Int): Marker {
        marker?.setDrawable(drawableId)
        return this
    }

    fun setIcon(@NonNull mapfitMarker: MapfitMarker): Marker {
        setIcon(mapfitMarker.getMarkerUrl())
        return this
    }

    /**
     * Set icon with a url consist of a image.
     */
    fun setIcon(imageUrl: String): Marker {

        launch {
            val drawable = loadImageFromUrl(imageUrl)

            async(UI) {
                marker?.setDrawable(drawable.await())
            }
        }

        return this
    }

    fun setStyle(style: String): Marker {
        marker?.setStylingFromString(style)
        return this
    }

    override fun setZIndex(index: Int) {}

    override fun hide() {}

    override fun show() {}

    override fun getId(): Long? = marker?.markerId

    override fun getLocation(): LatLng = mlocation

}