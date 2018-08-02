package com.mapfit.android.annotations

import android.graphics.PointF
import com.mapfit.android.geometry.LatLng

internal data class Building @JvmOverloads constructor(
    var position: PointF = PointF(),
    var latLng: LatLng = LatLng(),
    var id: Long = 0L,
    var rootId: Long = 0L
)