package com.mapfit.android.compass

/**
 * Interface to listen orientation change events. @see [CompassProvider]
 *
 * Created by dogangulcan on 3/5/21.
 */
internal interface CompassListener {

    fun onOrientationChanged(angle: Float)

}