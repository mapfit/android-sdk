package com.mapfit.demo

import android.app.Application
import com.mapfit.android.Mapfit
import com.mapfit.mapfitdemo.R

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))

    }

}