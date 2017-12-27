package com.mapfit.mapfitsdk

import android.content.Context

/**
 * Created by dogangulcan on 12/18/17.
 */
class Mapfit(val context: Context, val apiKey: String = "") {

    @Volatile private var mapfitInstance: Mapfit? = null

    fun initialize(context: Context, apiKey: String): Mapfit {
        return synchronized(this) {
            mapfitInstance ?: Mapfit(context, apiKey)
        }
    }


}