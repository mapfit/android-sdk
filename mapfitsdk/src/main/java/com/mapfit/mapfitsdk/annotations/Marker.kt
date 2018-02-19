package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.util.Log
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.R
import com.mapfit.mapfitsdk.annotations.widget.PlaceInfo
import com.mapfit.mapfitsdk.geocoder.model.Address
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.LatLngBounds
import com.mapfit.mapfitsdk.geometry.isValid
import com.mapfit.mapfitsdk.utils.getBitmapFromVectorDrawable
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.launch

/**
 * Markers are icons placed on a particular location on the map.
 *
 * Created by dogangulcan on 12/19/17.
 */
class Marker internal constructor(
    private val context: Context,
    markerId: Long,
    private val mapController: MapController
) : Annotation(markerId, mapController) {

    private var position: LatLng = LatLng(0.0, 0.0)
    val markerOptions = MarkerOptions(this, mutableListOf(mapController))
    internal var usingDefaultIcon: Boolean = true
    internal var placeInfoMap = HashMap<MapController, PlaceInfo?>()
    internal var address: Address? = null
        set(value) {
            field = value
            if (title.isBlank() && value != null) {
                title = value.streetAddress
            }
        }

    private var icon: Bitmap? = null
    private var previousIcon: Bitmap? = null
    private var iconChangedWhenPlaceInfo: Bitmap? = null

    private var title: String = ""
    private var subtitle1: String = ""
    private var subtitle2: String = ""

    fun getTitle() = title
    fun getSubtitle1() = subtitle1
    fun getSubtitle2() = subtitle2

    /**
     * Sets the title to be shown on place info.
     *
     * @param title
     */
    fun setTitle(title: String): Marker {
        this.title = title
        updatePlaceInfoFields()

        return this
    }

    /**
     * Sets the first subtitle to be shown on place info.
     *
     * @param subtitle1
     */
    fun setSubtitle1(subtitle1: String): Marker {
        this.subtitle1 = subtitle1
        updatePlaceInfoFields()
        return this
    }

    /**
     * Sets the second subtitle to be shown on place info.
     *
     * @param subtitle2
     */
    fun setSubtitle2(subtitle2: String): Marker {
        this.subtitle2 = subtitle2
        updatePlaceInfoFields()
        return this
    }

    private fun updatePlaceInfoFields() {
        placeInfoMap.values.forEach {
            it?.updatePlaceInfo()
        }
    }

    init {
        setIcon(MapfitMarker.DEFAULT)
        initAnnotation(mapController, markerId)
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        if (!markerOptions.mapController.contains(mapController)) {
            markerOptions.mapController.add(mapController)
        }
        mapBindings[mapController] = id
        markerOptions.updateStyle()
        icon?.let { setBitmap(it, mapController, id) }
        setPosition(position)
    }

    /**
     * @return current position of the marker
     */
    fun getPosition(): LatLng = position

    /**
     * Sets the position of marker to the given [LatLng].
     *
     * @param latLng
     */
    fun setPosition(latLng: LatLng): Marker {
        if (latLng.isValid()) {
            mapBindings.forEach {
                val markerPositionSet = it.key.setMarkerPointEased(
                    it.value,
                    latLng.lon,
                    latLng.lat,
                    0,
                    MapController.EaseType.CUBIC
                )

                updatePosition(markerPositionSet, latLng)
            }
        }

        return this
    }

    private fun setPositionEased(latLng: LatLng, duration: Int): Marker {
        if (latLng.isValid()) {
            mapBindings.forEach {
                val markerPositionSet = it.key.setMarkerPointEased(
                    it.value,
                    latLng.lon,
                    latLng.lat,
                    duration,
                    MapController.EaseType.CUBIC
                )

                updatePosition(markerPositionSet, latLng)
            }
        }
        return this
    }

    private fun updatePosition(markerPositionSet: Boolean, latLng: LatLng) {
        if (markerPositionSet) {
            position = latLng
        } else {
            Log.e(
                "Mapfit",
                "Setting Marker position is failed for ${latLng.lat}, ${latLng.lon}"
            )
        }
    }

    /**
     * Sets the marker icon with the given drawable.
     *
     * @param drawable
     */
    fun setIcon(drawable: Drawable): Marker {
        val density = context.resources.displayMetrics.densityDpi
        val bitmapDrawable = drawable as BitmapDrawable
        bitmapDrawable.setTargetDensity(density)
        val bitmap = bitmapDrawable.bitmap
        bitmap.density = density
        setBitmap(bitmap, mapController)
        usingDefaultIcon = false
        return this
    }

    /**
     * Sets the marker icon with the given drawable resource id.
     *
     * @param drawableId
     */
    fun setIcon(@DrawableRes drawableId: Int): Marker {
        val options = BitmapFactory.Options()
        options.inTargetDensity = context.resources.displayMetrics.densityDpi
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId, options)
        setBitmap(bitmap, mapController)
        usingDefaultIcon = false
        return this
    }

    /**
     * Sets the marker icon with the given [MapfitMarker].
     *
     * @param mapfitMarker
     */
    fun setIcon(@NonNull mapfitMarker: MapfitMarker): Marker {
        setIcon(mapfitMarker.getUrl())
        markerOptions.setDefaultMarkerSize()
        return this
    }

    /**
     * Sets the marker icon with the given image URL.
     *
     * @param imageUrl
     */
    fun setIcon(imageUrl: String): Marker {
        launch {
            val drawable = loadImageFromUrl(imageUrl)
            drawable.await()?.let {
                setIcon(it)
                usingDefaultIcon = false
            }
        }
        return this
    }

    internal fun placeInfoState(
        shown: Boolean,
        mapController: MapController
    ) {

        val placeInfo = placeInfoMap[mapController]
        placeInfo?.apply {

            val markerId = getIdForMap(mapController) ?: 0

            if (shown) {
                setBitmap(
                    getBitmapFromVectorDrawable(context, R.drawable.ic_marker_dot),
                    mapController,
                    markerId
                )
                markerOptions.placeInfoShown(shown, markerId, mapController)

            } else {

                if (getVisible(mapController)) {
                    setBitmap(iconChangedWhenPlaceInfo ?: previousIcon!!, mapController, markerId)
                    markerOptions.placeInfoShown(shown, markerId, mapController)
                    placeInfoMap.remove(mapController)
                    iconChangedWhenPlaceInfo?.let {
                        previousIcon = it
                        iconChangedWhenPlaceInfo = null
                    }
                }
            }
        }
    }

    private fun setBitmap(
        bitmap: Bitmap,
        mapController: MapController,
        markerId: Long = 0
    ) {

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

        if (markerId != 0L) {
            mapController.setMarkerBitmap(markerId, width, height, abgr)
        } else {

            mapBindings.forEach {

                val activePlaceInfoMarkerId =
                    placeInfoMap[it.key]?.marker?.mapBindings?.get(it.key) ?: 0L

                if (it.value != activePlaceInfoMarkerId) {
                    it.key.setMarkerBitmap(it.value, width, height, abgr)
                    previousIcon = if (previousIcon == null) bitmap else icon
                    icon = bitmap
                } else {
//                    previousIcon = bitmap
                    iconChangedWhenPlaceInfo = bitmap
                }
            }
        }
    }

    /**
     * Removes the marker from every [Layer] and [MapView] it is added to.
     */
    override fun remove() {
        placeInfoMap.forEach {
            it.value?.dispose(true)
        }

        mapBindings.forEach {
            it.key.removeMarker(it.value)
        }

        layers.forEach { it.remove(this) }

        subAnnotation?.remove()
    }

    override fun getLatLngBounds(): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()

        boundsBuilder.include(position)

        subAnnotation
            ?.takeIf { it is Polygon }
            .let { (it as Polygon).points.forEach { it.forEach { boundsBuilder.include(it) } } }

        return boundsBuilder.build()
    }

    override fun remove(mapController: MapController) {
        subAnnotation?.remove(listOf(mapController))
        placeInfoMap[mapController]?.dispose()
        mapBindings[mapController]?.let { mapController.removeMarker(it) }
    }

    internal fun getScreenPosition(mapController: MapController): PointF {
        var screenPosition = PointF()
        mapBindings.filter { it.key == mapController }.keys.forEach {
            screenPosition = it.lngLatToScreenPosition(position)
        }
        return screenPosition
    }

    internal fun setPolygon(polygon: Polygon) {
        subAnnotation = polygon
    }

    internal fun hasPlaceInfoFields(): Boolean =
        title.isNotBlank() || subtitle1.isNotBlank() || subtitle2.isNotBlank()

}