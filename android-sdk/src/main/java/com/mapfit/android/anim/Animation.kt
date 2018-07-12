package com.mapfit.android.anim

import android.graphics.drawable.Animatable

abstract class Animation : Animatable {

    var running = false
    var finished = false
    var paused = false
    var cancelled = false

}