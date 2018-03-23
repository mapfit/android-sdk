package com.mapfit.android.compass

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


/**
 * Compass provider uses accelerometer and magnetometer.
 *
 * Created by dogangulcan on 3/5/18.
 */
internal class CompassProvider(
    context: Context,
    val compassListener: CompassListener
) {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val accelerometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val magnetometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)

    private val tempRotationMatrix = FloatArray(9)
    private val rotationMatrix = FloatArray(9)

    private val alpha = 0.97f
    internal var azimuth = 0f

    /**
     * Starts listening orientation changes.
     */
    fun start() {
        registerSensor(accelerometer)
        registerSensor(magnetometer)
    }

    /**
     * Stops listening orientation changes.
     */
    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun registerSensor(sensor: Sensor) {
        sensorManager.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            async {
                event?.let {

                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0]
                            geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1]
                            geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2]
                        }
                    }

                    val success = SensorManager.getRotationMatrix(
                        tempRotationMatrix,
                        null,
                        gravity,
                        geomagnetic
                    )

                    if (success) {
                        val orientation = FloatArray(3)

                        configureDeviceAngle()

                        SensorManager.getOrientation(rotationMatrix, orientation)
                        azimuth = Math.toDegrees(orientation[0].toDouble())
                            .toFloat()// horizontal direction
                        azimuth = (azimuth + 360) % 360

                        launch(UI) { compassListener.onOrientationChanged(azimuth) }
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }

    private fun configureDeviceAngle() {
        when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 // Portrait
            -> SensorManager.remapCoordinateSystem(
                tempRotationMatrix,
                SensorManager.AXIS_Z,
                SensorManager.AXIS_Y,
                rotationMatrix
            )
            Surface.ROTATION_90 // Landscape
            -> SensorManager.remapCoordinateSystem(
                tempRotationMatrix,
                SensorManager.AXIS_Y,
                SensorManager.AXIS_MINUS_Z,
                rotationMatrix
            )
            Surface.ROTATION_180 // Portrait
            -> SensorManager.remapCoordinateSystem(
                tempRotationMatrix,
                SensorManager.AXIS_MINUS_Z,
                SensorManager.AXIS_MINUS_Y,
                rotationMatrix
            )
            Surface.ROTATION_270 // Landscape
            -> SensorManager.remapCoordinateSystem(
                tempRotationMatrix,
                SensorManager.AXIS_MINUS_Y,
                SensorManager.AXIS_Z,
                rotationMatrix
            )
        }
    }


}

