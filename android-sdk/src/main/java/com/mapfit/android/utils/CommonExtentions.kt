package com.mapfit.android.utils

import android.content.Context
import android.content.Intent

/**
 * Created by dogangulcan on 1/23/18.
 */

internal fun Context.startActivitySafe(intent: Intent) {
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}
