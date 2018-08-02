package com.mapfit.android.anim

import android.graphics.drawable.Animatable

abstract class Animation : Animatable {

    var running = false
    var paused = false
    var canceled = false
    var finished = false

}