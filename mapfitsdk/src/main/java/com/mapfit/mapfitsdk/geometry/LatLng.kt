package com.mapfit.mapfitsdk.geometry

import android.os.Parcelable
import android.support.annotation.FloatRange
import kotlinx.android.parcel.Parcelize

/**
 * Immutable class holding a pair of latitude and longitude coordinates in degrees.
 *
 * @param lat Latitude, in degrees.
 * @param lon Longitude, in degrees.
 *
 * Created by dogangulcan on 12/19/17.
 */
@Parcelize
data class LatLng(
        @FloatRange(from = -90.0, to = 90.0) val lat: Double = 0.0,
        @FloatRange(from = -180.0, to = 180.0) val lon: Double = 0.0
) : Parcelable {
    init {
        isValid()
    }

}
