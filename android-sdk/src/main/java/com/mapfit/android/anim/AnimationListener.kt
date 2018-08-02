package com.mapfit.android.anim

import android.graphics.drawable.Animatable

interface AnimationListener {

    /**
     * Invoked on animation start.
     */
    fun onStart(animatable: Animatable)


    /**
     * Invoked on animation end.
     */
    fun onFinish(animatable: Animatable)

}