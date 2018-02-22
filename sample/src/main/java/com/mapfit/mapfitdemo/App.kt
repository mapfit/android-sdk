package com.mapfit.mapfitdemo

import android.app.Application
import com.mapfit.android.Mapfit

/**
 * Created by dogangulcan on 1/18/18.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val mapfit = Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
    }

}