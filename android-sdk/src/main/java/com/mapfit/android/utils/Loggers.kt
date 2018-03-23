package com.mapfit.android.utils

import android.util.Log
import com.mapfit.android.Mapfit

/**
 * Logging functions.
 *
 * Created by dogangulcan on 3/15/18.
 */


internal fun logWarning(s: String) {
    Log.w(Mapfit.TAG, s)
}

internal fun logError(s: String) {
    Log.e(Mapfit.TAG, s)
}