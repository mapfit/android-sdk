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
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.widget.PlaceInfo
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
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
import kotlinx.coroutines.experimental.android.UI

class Marker internal constructor(
    private val context: Context,
    private val markerOptions: MarkerOptions,
    markerId: Long,
    private val mapController: MapController
) : Annotation(markerId, mapController) {

    private var previousIcon: Bitmap? = null
    private var iconChangedWhenPlaceInfo: Bitmap? = null
    private var iconPlacementJob = Job()
    private val density = context.resources.displayMetrics.densityDpi
    private val iconContext = newSingleThreadContext("icon_context")

    internal var placeInfoMap = HashMap<MapController, PlaceInfo?>()
    internal var hasCustomPlaceInfo: Boolean = false

    var height = markerOptions.height
        set(value) {
            field = value
            updateStyle()
        }

    var width = markerOptions.width
        set(value) {
            field = value
            updateStyle()
        }

    var drawOrder = markerOptions.drawOrder
        set(value) {
            field = value
            updateStyle()
        }

    var flat = markerOptions.flat
        set(value) {
            field = value
            updateStyle()
        }

    var interactive = markerOptions.interactive
        set(value) {
            field = value
            updateStyle()
        }

    var anchor = markerOptions.anchor
        set(value) {
            field = value
            updateStyle()
        }

    var position = markerOptions.position
        set(value) {
            if (value.isValid()) {
                field = value

                mapBindings.forEach { (mapController, markerId) ->
                    val markerPositionSet = mapController.setMarkerPoint(
                        markerId,
                        field.lng,
                        field.lat
                    )

                    checkPositionUpdate(markerPositionSet, field)
                }
            }
        }

    var title = markerOptions.title
        set(value) {
            field = value
            updatePlaceInfoFields()
        }

    var subtitle1 = markerOptions.subtitle1
        set(value) {
            field = value
            updatePlaceInfoFields()
        }

    var subtitle2 = markerOptions.subtitle2
        set(value) {
            field = value
            updatePlaceInfoFields()
        }

    var streetAddress = markerOptions.streetAddress

    var address: Address? = null

    var buildingPolygon: Polygon? = null
        internal set(value) {
            field = value
            subAnnotation = field
        }
        get() {
            return subAnnotation as Polygon?
        }

    init {
        data = markerOptions.data

        val icon = markerOptions.getIcon()

        when (icon) {
            is Drawable -> setIcon(icon)
            is Bitmap -> setIcon(icon)
            is String -> setIcon(icon)
            is Int -> setIcon(icon)
            is MapfitMarker -> setIcon(icon)
            else -> setIcon(MapfitMarker.DEFAULT)
        }

        initAnnotation(mapController, markerId)
    }

    override fun initAnnotation(mapController: MapController, id: Long) {
        mapBindings[mapController] = id

        updateStyle()

        previousIcon?.let { setBitmap(it, mapController, id) }

        if (!position.isEmpty()) position = position
    }

    override fun getLatLngBounds(): LatLngBounds {
        val boundsBuilder = LatLngBounds.Builder()

        boundsBuilder.include(position)

        subAnnotation
            ?.takeIf { it is Polygon }
            ?.let { (it as Polygon).points.forEach { it.forEach { boundsBuilder.include(it) } } }

        return boundsBuilder.build()
    }

    /**
     * Sets the marker icon with the given image URL.
     *
     * @param imageUrl
     */
    fun setIcon(imageUrl: String) = runBlocking(iconContext) {
        iconPlacementJob.cancelAndJoin()
        iconPlacementJob = launch {
            val drawable = loadImageFromUrl(imageUrl)
            drawable.await()
                ?.let {
                    val bitmap = it.toBitmap(this@Marker.context)
                    setBitmap(bitmap, mapController)
                }
        }
    }

    /**
     * Sets the marker icon with the given bitmap.
     *
     * @param bitmap
     */
    fun setIcon(bitmap: Bitmap) = runBlocking(iconContext) {
        iconPlacementJob.cancelAndJoin()
        iconPlacementJob = launch {
            setBitmap(bitmap)
        }
    }

    /**
     * Sets the marker icon with the given drawable.
     *
     * @param drawable
     */
    fun setIcon(drawable: Drawable) = runBlocking(iconContext) {
        iconPlacementJob.cancelAndJoin()
        iconPlacementJob = launch {
            val bitmap = drawable.toBitmap(this@Marker.context)

            setBitmap(bitmap)
        }
    }

    /**
     * Sets the marker icon with the given drawable resource id.
     *
     * @param drawableId
     */
    fun setIcon(@DrawableRes drawableId: Int) = runBlocking(iconContext) {
        iconPlacementJob.cancelAndJoin()
        iconPlacementJob = launch {
            val bitmap = getBitmapFromDrawableID(this@Marker.context, drawableId)
                    ?: getBitmapFromVectorDrawable(this@Marker.context, drawableId)

            setBitmap(bitmap)
        }
    }

    /**
     * Sets the marker icon with the given [MapfitMarker].
     *
     * @param mapfitMarker
     */
    fun setIcon(@NonNull mapfitMarker: MapfitMarker) = runBlocking(iconContext) {
        iconPlacementJob.cancelAndJoin()
        iconPlacementJob = launch {
            val drawable = loadImageFromUrl(mapfitMarker.getUrl())
            drawable.await()
                ?.takeIf { !iconPlacementJob.isCancelled }
                ?.let {
                    val bitmap = it.toBitmap(this@Marker.context)
                    setBitmap(bitmap)
                    setDefaultMarkerSize()
                }
        }
    }

    /**
     * Sets the given bitmap as marker icon of the Marker.
     */
    private fun setBitmap(
        bitmap: Bitmap,
        mapController: MapController? = null,
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
            mapController?.setMarkerBitmap(markerId, width, height, abgr)

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

                placeInfoShown(shown, markerId, mapController)

            } else {
                if (getVisibility(mapController)) {
                    setBitmap(iconChangedWhenPlaceInfo ?: previousIcon!!, mapController, markerId)

                    placeInfoShown(shown, markerId, mapController)

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
     *
     */
    fun setPositionEased(latLng: LatLng, duration: Int) {
        if (latLng.isValid()) {
            mapBindings.forEach {
                val markerPositionSet = it.key.setMarkerPointEased(
                    it.value,
                    latLng.lng,
                    latLng.lat,
                    duration,
                    MapController.EaseType.CUBIC
                )

                checkPositionUpdate(markerPositionSet, latLng)

            }
        }
    }

    /**
     * Sets the width and height of the marker icon.
     *
     * @param width in pixels
     * @param height in pixels
     */
    fun setSize(width: Int, height: Int) {
        val styleString = getStyleString(width, height)

        mapBindings.forEach {
            it.key.setMarkerStylingFromString(it.value, styleString)
            it.key.setMarkerDrawOrder(it.value, drawOrder)
        }
    }

    private fun placeInfoShown(
        isShown: Boolean,
        markerId: Long,
        mapController: MapController
    ) {
        if (isShown) {
            mapController.setMarkerStylingFromString(markerId, getPlaceInfoMarkerStyle())

        } else {
            mapController.setMarkerStylingFromString(markerId, getStyleString())
        }
    }

    private fun setDefaultMarkerSize() {
        height = 59
        width = 55
    }

    private fun updateStyle() {
        val styleString = getStyleString()

        mapBindings.forEach {
            it.key.setMarkerStylingFromString(it.value, styleString)
            it.key.setMarkerDrawOrder(it.value, drawOrder)
        }
    }

    private fun getPlaceInfoMarkerStyle() =
        "{ style: 'sdk-point-overlay', " +
                "anchor: top, " +
                "size: [11px, 11px], " +
                "order: $drawOrder, " +
                "interactive: true, " +
                "collide: false }"

    private fun getStyleString() =
        "{ style: 'sdk-point-overlay', " +
                "anchor: ${anchor.getAnchor()}," +
                "flat: $flat, " +
                "size: [${width}px, ${height}px]," +
                "order: $drawOrder, " +
                "interactive: $interactive, " +
                "collide: false }"

    private fun getStyleString(width: Int, height: Int) =
        "{ style: 'sdk-point-overlay', " +
                "anchor: ${anchor.getAnchor()}," +
                "flat: $flat, " +
                "size: [${width}px, ${height}px]," +
                "order: $drawOrder, " +
                "interactive: $interactive, " +
                "collide: false }"

    private fun updatePlaceInfoFields() = placeInfoMap.values.forEach { it?.updatePlaceInfo() }

    @Synchronized
    internal fun setAccuracyMarkerStyle(
        w: Int,
        h: Int
    ) {
        mapBindings.forEach {
            it.key.setMarkerStylingFromString(
                it.value,
                "{ style: 'sdk-point-overlay', " +
                        "anchor: ${anchor.getAnchor()}, " +
                        "flat: $flat, size: [${w}px, ${h}px], " +
                        "order: $drawOrder, " +
                        "interactive: $interactive, " +
                        "collide: false }"
            )
        }
    }

    private fun checkPositionUpdate(markerPositionSet: Boolean, latLng: LatLng) {
        if (!markerPositionSet) {
            Log.e(
                "Mapfit",
                "Setting Marker position is failed for ${latLng.lat}, ${latLng.lng}"
            )
        }
    }

    /**
     * Removes the marker from every [Layer] and [MapView] it is added to.
     */
    override fun remove() {
        placeInfoMap.forEach { it.value?.dispose(true) }

        val toBeRemoved = mutableListOf<MapController>()

        mapBindings.forEach {
            it.key.removeMarker(it.value)
            toBeRemoved.add(it.key)
        }

        toBeRemoved.forEach { mapBindings.remove(it) }

        layers.forEach { it.remove(this) }

        subAnnotation?.remove()
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
            screenPosition = it.latLngToScreenPosition(position)
        }
        return screenPosition
    }

    internal fun hasPlaceInfoFields(): Boolean =
        title.isNotBlank()
                || subtitle1.isNotBlank()
                || subtitle2.isNotBlank()


    internal fun geocode(callback: OnMarkerAddedCallback?) = launch {
        Geocoder().geocode(
            streetAddress,
            markerOptions.buildingPolygon,
            object : GeocoderCallback {
                override fun onSuccess(addressList: List<Address>) {
                    if (addressList.isEmpty()) {
                        callback?.onError(Exception("No address is found."))

                    } else {
                        val latLng = addressList.first().getPrimaryEntrance()

                        if (latLng.isEmpty()) {
                            callback?.onError(Exception("No entrance found for given address."))

                        } else {
                            position = latLng
                            address = addressList.first()

                            if (addressList.isNotEmpty()
                                && addressList.first().building.polygon.isNotEmpty()
                            ) {
                                val polygonOptions =
                                    markerOptions.buildingPolygonOptions ?: PolygonOptions()
                                polygonOptions.points(addressList.first().building.polygon)

                                buildingPolygon = mapController.addPolygon(polygonOptions)
                            }

                            launch(UI) { callback?.onMarkerAdded(this@Marker) }
                        }
                    }
                }

                override fun onError(message: String, e: Exception) {
                    launch(UI) { callback?.onError(e) }
                }
            })
    }

}
