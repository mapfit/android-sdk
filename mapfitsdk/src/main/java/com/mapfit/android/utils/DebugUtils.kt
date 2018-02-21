package com.mapfit.android.utils

import com.mapfit.android.BuildConfig

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