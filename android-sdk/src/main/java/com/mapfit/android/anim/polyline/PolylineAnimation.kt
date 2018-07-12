package com.mapfit.android.anim.polyline

import com.mapfit.android.anim.Animation
import com.mapfit.android.annotations.Polyline

abstract class PolylineAnimation : Animation() {

    lateinit var polyline: Polyline

}