package com.mapfit.mapfitsdk.utils

import com.mapfit.mapfitsdk.BuildConfig

/**
 * Created by dogangulcan on 1/23/18.
 */
class DebugUtils {
    companion object {

        fun logException(e: Exception) {
            if (BuildConfig.DEBUG_MODE) {
                e.printStackTrace()
            }
        }

    }

}