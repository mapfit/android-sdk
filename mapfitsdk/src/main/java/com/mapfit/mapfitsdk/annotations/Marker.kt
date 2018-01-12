package com.mapfit.mapfitsdk.annotations

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.util.Log
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.launch

/**
 * Created by dogangulcan on 12/19/17.
 */
class Marker internal constructor(private val tgMarker: com.mapzen.tangram.Marker? = null) : Annotation() {


    var position: LatLng = LatLng(0.0, 0.0)

    var isFlat: Boolean = false

    val markerOptions = MarkerOptions(tgMarker)

    var data: Any? = null
        set(value) {
            tgMarker?.userData = value
            field = value
        }

    init {
        setIcon(MapfitMarker.LIGHT_DEFAULT)
        tgMarker?.setStylingFromString(markerOptions.style)

    }

    fun setPosition(latLng: LatLng): Marker {
        position = latLng
        return this
    }

    fun setIcon(drawable: Drawable): Marker {
        tgMarker?.setDrawable(drawable)
        return this
    }

    fun setIcon(@DrawableRes drawableId: Int): Marker {
        tgMarker?.setDrawable(drawableId)
        return this
    }

    fun setIcon(@NonNull mapfitMarker: MapfitMarker): Marker {
        setIcon(mapfitMarker.getMarkerUrl())
        return this
    }

    /**
     * Set icon with a url consist of a image.
     */
    @Synchronized
    fun setIcon(imageUrl: String): Marker {
        Log.i("tgMarker", "setIcon called")

        launch {
            val drawable = loadImageFromUrl(imageUrl)
            drawable.join()
            tgMarker?.setDrawable(drawable.await())
        }


        return this
    }

    override fun setDrawOder(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVisible(visible: Boolean) {
        tgMarker?.isVisible = visible
    }

    override fun getId(): Long? = tgMarker?.markerId

    internal fun getMarker(): com.mapzen.tangram.Marker? {
        return tgMarker
    }

    fun setData(data: Any): Marker {
        this.data = data
        return this
    }

}