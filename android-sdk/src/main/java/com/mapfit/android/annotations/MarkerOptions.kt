package com.mapfit.android.annotations

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import com.mapfit.android.geometry.LatLng

/**
 *  Defines options for [Marker].
 *
 *  Created by dogangulcan on 5/8/18.
 */
class MarkerOptions {

    internal var height = 59
    internal var width = 55
    internal var streetAddress = ""
    internal var position = LatLng(0.0, 0.0)
    internal var flat = false
    internal var tag = Any()
    internal var buildingPolygon = false
    internal var buildingPolygonOptions: PolygonOptions? = null
    internal var interactive = true
    internal var visible = true
    internal var drawOrder = 2000
    internal var anchor = Anchor.TOP
    internal var title = ""
    internal var subtitle1 = ""
    internal var subtitle2 = ""
    internal var iconUrl = ""
    internal var iconDrawable: Drawable? = null
    internal var iconBitmap: Bitmap? = null
    internal var iconDrawableId = 0
    internal var iconMapfit: MapfitMarker? = null
    internal var geocode = false
    internal var reverseGeocode = false

    /**
     * Height of the marker icon.
     *
     * @param height as pixels
     */
    fun height(height: Int): MarkerOptions {
        this.height = height
        return this
    }

    /**
     * Width of the marker icon.
     *
     * @param width as pixels
     */
    fun width(width: Int): MarkerOptions {
        this.width = width
        return this
    }

    /**
     * Street address of the marker. The address can be geocoded and the marker
     * position will be set to entrance point of the building if applicable.
     *
     * @param address street address
     * @param geocode if true, the address will be geocoded.
     */
    @JvmOverloads
    fun streetAddress(
        address: String,
        geocode: Boolean = false
    ): MarkerOptions {
        this.streetAddress = address
        this.geocode = geocode
        return this
    }

    /**
     * Position of the marker.
     *
     * @param position of the [Marker] as [LatLng]
     * @param reverseGeocode if true, position will be reverse geocoded
     */
    @JvmOverloads
    fun position(
        position: LatLng,
        reverseGeocode: Boolean = false
    ): MarkerOptions {
        this.position = position
        this.reverseGeocode = reverseGeocode
        return this
    }

    /**
     * Sets the marker icons dimension. It can be flat or 3D.
     *
     * @param flat set true for flat marker icon
     */
    fun flat(flat: Boolean): MarkerOptions {
        this.flat = flat
        return this
    }

    /**
     * Sets the given object related with the [Marker]. Setting related object of marker as a tag makes it
     * easier to reach the object rather than storing a Map data structure.
     *
     * @param tag any object related to the marker
     */
    fun tag(tag: Any): MarkerOptions {
        this.tag = tag
        return this
    }

    /**
     * Adds building polygon for the marker position or address if applicable. Setting building
     * polygon true forces geocoding the address or reverse geocoding the position.
     *
     * @param buildingPolygon if set true, building polygon will be added to the marker
     * @param options optional options for the building polygon style
     */
    @JvmOverloads
    fun addBuildingPolygon(
        buildingPolygon: Boolean,
        options: PolygonOptions? = null
    ): MarkerOptions {
        this.buildingPolygon = buildingPolygon
        this.buildingPolygonOptions = options
        return this
    }

    /**
     * Sets if the marker will be interactive or not. Non-interactive markers are not clickable and
     * won't be accessible trough the click listeners.
     *
     * @param interactive
     */
    fun interactive(interactive: Boolean): MarkerOptions {
        this.interactive = interactive
        return this
    }

    /**
     * Sets if marker is visible or invisible.
     *
     * @param visible true if visible
     */
    fun visible(visible: Boolean): MarkerOptions {
        this.visible = visible
        return this
    }

    /**
     * Sets the marker's drawing order. The marker with higher draw order will be drawn above
     * the ones have lesser draw order.
     *
     * @param drawOrder of the marker
     */
    fun drawOrder(drawOrder: Int): MarkerOptions {
        this.drawOrder = drawOrder
        return this
    }

    /**
     * Anchor of the Marker. Marker will be fixed to the given anchor point.
     *
     * @param anchor
     */
    fun anchor(anchor: Anchor): MarkerOptions {
        this.anchor = anchor
        return this
    }

    /**
     * Title of the marker for the place info card. If the title is empty, place info won't be
     * shown.
     *
     * @param title
     */
    fun title(title: String): MarkerOptions {
        this.title = title
        return this
    }

    /**
     * First subtitle of the marker's place info card.
     *
     * @param subtitle1
     */
    fun subtitle1(subtitle1: String): MarkerOptions {
        this.subtitle1 = subtitle1
        return this
    }

    /**
     * Second subtitle of the marker's place info card.
     *
     * @param subtitle2
     */
    fun subtitle2(subtitle2: String): MarkerOptions {
        this.subtitle2 = subtitle2
        return this
    }

    /**
     * Sets the marker icon with the given drawable.
     *
     * @param drawable
     */
    fun icon(drawable: Drawable): MarkerOptions {
        this.iconDrawable = drawable
        return this
    }

    /**
     * Sets the marker icon with the given drawable resource id.
     *
     * @param drawableId
     */
    fun icon(@DrawableRes drawableId: Int): MarkerOptions {
        this.iconDrawableId = drawableId
        return this
    }

    /**
     * Sets the marker icon with the given [MapfitMarker].
     *
     * @param mapfitMarker
     */
    fun icon(mapfitMarker: MapfitMarker): MarkerOptions {
        this.iconMapfit = mapfitMarker
        return this
    }

    /**
     * Sets the marker icon with the given image URL.
     *
     * @param url
     */
    fun icon(url: String): MarkerOptions {
        this.iconUrl = url
        return this
    }

    /**
     * Sets the marker icon with the given bitmap.
     *
     * @param bitmap
     */
    fun icon(bitmap: Bitmap): MarkerOptions {
        this.iconBitmap = bitmap
        return this
    }

    internal fun getIcon(): Any = when {
        iconMapfit != null -> iconMapfit as MapfitMarker
        iconUrl.isNotBlank() -> iconUrl
        iconBitmap != null -> iconBitmap as Bitmap
        iconDrawable != null -> iconDrawable as Drawable
        iconDrawableId != 0 -> iconDrawableId
        else -> Any()
    }

}

