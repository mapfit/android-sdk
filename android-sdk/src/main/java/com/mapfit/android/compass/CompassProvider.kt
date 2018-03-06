package com.mapfit.android.compass

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager

/**
 * Created by dogangulcan on 3/5/18.
 */
class CompassProvider(context: Context) {
    val mSensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    fun abc() {


        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }
}