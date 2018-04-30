package com.mapfit.android.annotations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.util.Log
import com.mapfit.android.MapController
import com.mapfit.android.R
import com.mapfit.android.annotations.widget.PlaceInfo
import com.mapfit.android.geocoder.model.Address
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds
import com.mapfit.android.geometry.isEmpty
import com.mapfit.android.geometry.isValid
import com.mapfit.android.utils.getBitmapFromDrawableID
import com.mapfit.android.utils.getBitmapFromVectorDrawable
import com.mapfit.android.utils.loadImageFromUrl
import com.mapfit.android.utils.toBitmap
import kotlinx.coroutines.experimental.*

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

    companion object {
        private const val BUILDING_TAG = "building"
    }

    private var previousIcon: Bitmap? = null
    private var iconChangedWhenPlaceInfo: Bitmap? = null
    private var iconPlacementJob = Job()
    private var position: LatLng = LatLng(0.0, 0.0)
    private val density = this@Marker.context.resources.displayMetrics.densityDpi
    private var isInitialized = false
    private val iconContext = newSingleThreadContext("icon_context")

    private var title: String = ""
    private var subtitle1: String = ""
    private var subtitle2: String = ""

    internal var hasCustomPlaceInfo: Boolean = false
    internal var placeInfoMap = HashMap<MapController, PlaceInfo?>()
    internal var address: Address? = null
        set(value) {
            field = value
            if (title.isBlank() && value != null) {
                title = value.streetAddress
            }
        }

    init {
        setIcon(MapfitMarker.DEFAULT)
        initAnnotation(mapController, markerId)
        isInitialized = true
    }

    val markerOptions = MarkerOptions(this)

    var buildingPolygon: Polygon? = null
        internal set(value) {
            field = value
            field?.let { it.tag = BUILDING_TAG }
            subAnnotation = field
        }
        get() {
            return if (subAnnotation?.tag.equals(BUILDING_TAG)) {
                subAnnotation as Polygon
            } else {
                null
            }
        }

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

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = id
        if (isInitialized) {
            markerOptions.updateStyle()
        }


        previousIcon?.let { setBitmap(it, mapController, id) }

        if (!position.isEmpty()) setPosition(position)
    }

    /**
     * Get the geographical coordinate position of the marker.
     *
     * @return latLng
     */
    fun getPosition(): LatLng = position

    /**
     * Sets the position of marker to the given [LatLng].
     *
     * @param latLng
     */
    fun setPosition(latLng: LatLng): Marker {
        if (latLng.isValid()) {
            mapBindings.forEach { (mapController, markerId) ->
                val markerPositionSet = mapController.setMarkerPoint(
                    markerId,
                    latLng.lng,
                    latLng.lat
                )

                updatePosition(markerPositionSet, latLng)
            }
        }

        return this
    }

    internal fun setPositionEased(latLng: LatLng, duration: Int): Marker {
        if (latLng.isValid()) {
            mapBindings.forEach {
                val markerPositionSet = it.key.setMarkerPointEased(
                    it.value,
                    latLng.lng,
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
                "Setting Marker position is failed for ${latLng.lat}, ${latLng.lng}"
            )
        }
    }

    /**
     * Sets the marker icon with the given drawable.
     *
     * @param drawable
     */
    fun setIcon(drawable: Drawable): Marker {
        runBlocking(iconContext) {
            iconPlacementJob.cancelAndJoin()
            iconPlacementJob = launch {
                val bitmap = drawable.toBitmap(this@Marker.context)
                setBitmap(bitmap, mapController)
            }
        }
        return this
    }

    /**
     * Sets the marker icon with the given drawable resource id.
     *
     * @param drawableId
     */
    fun setIcon(@DrawableRes drawableId: Int): Marker {
        runBlocking(iconContext) {
            iconPlacementJob.cancelAndJoin()
            iconPlacementJob = launch {
                val bitmap = async {
                    getBitmapFromDrawableID(this@Marker.context, drawableId)
                            ?: getBitmapFromVectorDrawable(this@Marker.context, drawableId)
                }

                bitmap.await()
                    .takeIf { !iconPlacementJob.isCancelled }
                    ?.let { setBitmap(it, mapController) }
            }
        }
        return this
    }


    /**
     * Sets the marker icon with the given [MapfitMarker].
     *
     * @param mapfitMarker
     */
    fun setIcon(@NonNull mapfitMarker: MapfitMarker): Marker {
        runBlocking(iconContext) {
            iconPlacementJob.cancelAndJoin()
            iconPlacementJob = launch {
                val drawable = loadImageFromUrl(mapfitMarker.getUrl())
                drawable.await()
                    ?.takeIf { !iconPlacementJob.isCancelled }
                    ?.let {
                        val bitmap = it.toBitmap(this@Marker.context)
                        setBitmap(bitmap, mapController)
                        markerOptions.setDefaultMarkerSize()
                    }
            }
        }
        return this
    }

    /**
     * Sets the marker icon with the given image URL.
     *
     * @param imageUrl
     */
    fun setIcon(imageUrl: String): Marker {
        runBlocking(iconContext) {
            iconPlacementJob.cancelAndJoin()
            iconPlacementJob = launch {
                val drawable = loadImageFromUrl(imageUrl)
                drawable.await()
                    ?.takeIf { !iconPlacementJob.isCancelled }
                    ?.let {
                        val bitmap = it.toBitmap(this@Marker.context)
                        setBitmap(bitmap, mapController)
                    }
            }
        }
        return this
    }

    /**
     * Toggle function for showing/hiding place info.
     *
     * @param shown true will show, false will hide
     * @param mapController that marker is belong to
     */
    internal fun placeInfoState(
        shown: Boolean,
        mapController: MapController
    ) {

        val placeInfo = placeInfoMap[mapController]
        placeInfo?.apply {
            val markerId = getIdForMap(mapController) ?: 0

            if (shown) {
                setBitmap(
                    getBitmapFromVectorDrawable(context, R.drawable.mf_marker_dot),
                    mapController,
                    markerId
                )

                markerOptions.placeInfoShown(shown, markerId, mapController)

            } else {
                if (getVisibility(mapController)) {
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

    /**
     * Sets the given bitmap as marker icon of the Marker.
     */
    internal fun setBitmap(
        bitmap: Bitmap,
        mapController: MapController,
        markerId: Long = 0
    ) {
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
                } else {
                    iconChangedWhenPlaceInfo = bitmap
                }

                previousIcon = bitmap
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

        val toBeRemoved = mutableListOf<MapController>()

        mapBindings.forEach {
            it.key.removeMarker(it.value)
            toBeRemoved.add(it.key)
        }

        toBeRemoved.forEach { mapBindings.remove(it) }

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
        mapBindings[mapController]?.let {
            mapController.removeMarker(it)
        }
        mapBindings.remove(mapController)
    }

    internal fun getScreenPosition(mapController: MapController): PointF {
        var screenPosition = PointF()
        mapBindings.filter { it.key == mapController }.keys.forEach {
            screenPosition = it.lngLatToScreenPosition(position)
        }
        return screenPosition
    }

    internal fun hasPlaceInfoFields(): Boolean =
        title.isNotBlank() || subtitle1.isNotBlank() || subtitle2.isNotBlank()

}