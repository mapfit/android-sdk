package com.mapfit.android

import android.graphics.Color
import android.graphics.Point
import android.os.SystemClock
import android.support.test.espresso.InjectEventSecurityException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import android.support.test.espresso.matcher.ViewMatchers
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matcher


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
        MotionEvent.BUTTON_PRIMARY
    )
}

fun pinchOut(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isEnabled()
        }

        override fun getDescription(): String {
            return "Pinch out"
        }

        override fun perform(uiController: UiController, view: View) {
            val middlePosition = getCenterPoint(view)

            val startDelta = 0 // How far from the center point each finger should start
            val endDelta =
                500 // How far from the center point each finger should end (note: Be sure to have this large enough so that the gesture is recognized!)

            val startPoint1 = Point(middlePosition.x - startDelta, middlePosition.y)
            val startPoint2 = Point(middlePosition.x + startDelta, middlePosition.y)
            val endPoint1 = Point(middlePosition.x - endDelta, middlePosition.y)
            val endPoint2 = Point(middlePosition.x + endDelta, middlePosition.y)

            performPinch(uiController, startPoint1, startPoint2, endPoint1, endPoint2)
        }
    }
}

fun pinchIn(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return ViewMatchers.isEnabled()
        }

        override fun getDescription(): String {
            return "Pinch in"
        }

        override fun perform(uiController: UiController, view: View) {
            val middlePosition = getCenterPoint(view)

            val startDelta =
                500 // How far from the center point each finger should start (note: Be sure to have this large enough so that the gesture is recognized!)
            val endDelta = 0 // How far from the center point each finger should end

            val startPoint1 = Point(middlePosition.x - startDelta, middlePosition.y)
            val startPoint2 = Point(middlePosition.x + startDelta, middlePosition.y)
            val endPoint1 = Point(middlePosition.x - endDelta, middlePosition.y)
            val endPoint2 = Point(middlePosition.x + endDelta, middlePosition.y)

            performPinch(uiController, startPoint1, startPoint2, endPoint1, endPoint2)
        }
    }
}

private fun getCenterPoint(view: View): Point {
    val locationOnScreen = IntArray(2)
    view.getLocationOnScreen(locationOnScreen)
    val viewHeight = view.height * view.scaleY
    val viewWidth = view.width * view.scaleX
    return Point(
        (locationOnScreen[0] + viewWidth / 2).toInt(),
        (locationOnScreen[1] + viewHeight / 2).toInt()
    )
}

private fun performPinch(
    uiController: UiController,
    startPoint1: Point,
    startPoint2: Point,
    endPoint1: Point,
    endPoint2: Point
) {
    val duration = 500
    val eventMinInterval: Long = 10
    val startTime = SystemClock.uptimeMillis()
    var eventTime = startTime
    var event: MotionEvent
    var eventX1: Float = startPoint1.x.toFloat()
    var eventY1: Float = startPoint1.y.toFloat()
    var eventX2: Float = startPoint2.x.toFloat()
    var eventY2: Float = startPoint2.y.toFloat()

    // Specify the property for the two touch points
    val properties = arrayOfNulls<MotionEvent.PointerProperties>(2)
    val pp1 = MotionEvent.PointerProperties()
    pp1.id = 0
    pp1.toolType = MotionEvent.TOOL_TYPE_FINGER
    val pp2 = MotionEvent.PointerProperties()
    pp2.id = 1
    pp2.toolType = MotionEvent.TOOL_TYPE_FINGER

    properties[0] = pp1
    properties[1] = pp2

    // Specify the coordinations of the two touch points
    // NOTE: you MUST set the pressure and size value, or it doesn't work
    val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(2)
    val pc1 = MotionEvent.PointerCoords()
    pc1.x = eventX1
    pc1.y = eventY1
    pc1.pressure = 1f
    pc1.size = 1f
    val pc2 = MotionEvent.PointerCoords()
    pc2.x = eventX2
    pc2.y = eventY2
    pc2.pressure = 1f
    pc2.size = 1f
    pointerCoords[0] = pc1
    pointerCoords[1] = pc2

    /*
     * Events sequence of zoom gesture:
     *
     * 1. Send ACTION_DOWN event of one start point
     * 2. Send ACTION_POINTER_DOWN of two start rings
     * 3. Send ACTION_MOVE of two middle rings
     * 4. Repeat step 3 with updated middle rings (x,y), until reach the end rings
     * 5. Send ACTION_POINTER_UP of two end rings
     * 6. Send ACTION_UP of one end point
     */

    try {
        // Step 1
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_DOWN, 1, properties,
            pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 2
        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_DOWN + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2,
            properties,
            pointerCoords,
            0,
            0,
            1f,
            1f,
            0,
            0,
            0,
            0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 3, 4
        val moveEventNumber = duration / eventMinInterval

        val stepX1: Float
        val stepY1: Float
        val stepX2: Float
        val stepY2: Float

        stepX1 = ((endPoint1.x - startPoint1.x) / moveEventNumber).toFloat()
        stepY1 = ((endPoint1.y - startPoint1.y) / moveEventNumber).toFloat()
        stepX2 = ((endPoint2.x - startPoint2.x) / moveEventNumber).toFloat()
        stepY2 = ((endPoint2.y - startPoint2.y) / moveEventNumber).toFloat()

        for (i in 0 until moveEventNumber) {
            // Update the move events
            eventTime += eventMinInterval
            eventX1 += stepX1
            eventY1 += stepY1
            eventX2 += stepX2
            eventY2 += stepY2

            pc1.x = eventX1
            pc1.y = eventY1
            pc2.x = eventX2
            pc2.y = eventY2

            pointerCoords[0] = pc1
            pointerCoords[1] = pc2

            event = MotionEvent.obtain(
                startTime, eventTime,
                MotionEvent.ACTION_MOVE, 2, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
            )
            injectMotionEventToUiController(uiController, event)
        }

        // Step 5
        pc1.x = endPoint1.x.toFloat()
        pc1.y = endPoint1.y.toFloat()
        pc2.x = endPoint2.x.toFloat()
        pc2.y = endPoint2.y.toFloat()
        pointerCoords[0] = pc1
        pointerCoords[1] = pc2

        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_UP + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2,
            properties,
            pointerCoords,
            0,
            0,
            1f,
            1f,
            0,
            0,
            0,
            0
        )
        injectMotionEventToUiController(uiController, event)

        // Step 6
        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_UP, 1, properties,
            pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)
    } catch (e: InjectEventSecurityException) {
        throw RuntimeException("Could not perform pinch", e)
    }

}

@Throws(InjectEventSecurityException::class)
private fun injectMotionEventToUiController(uiController: UiController, event: MotionEvent) {
    val injectEventSucceeded = uiController.injectMotionEvent(event)
    if (!injectEventSucceeded) {
        throw IllegalStateException("Error performing event " + event)
    }
}
