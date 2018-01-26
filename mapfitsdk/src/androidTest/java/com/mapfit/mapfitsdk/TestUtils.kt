package com.mapfit.mapfitsdk

import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import android.view.InputDevice
import android.view.MotionEvent

/**
 * Created by dogangulcan on 1/26/18.
 */

fun clickOn(x: Int, y: Int): ViewAction {
    return GeneralClickAction(
            Tap.SINGLE,
            CoordinatesProvider { view ->
                val screenPos = IntArray(2)
                view?.getLocationOnScreen(screenPos)

                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()

                floatArrayOf(screenX, screenY)
            },
            Press.FINGER,
            InputDevice.SOURCE_MOUSE,
            MotionEvent.BUTTON_PRIMARY)
}
