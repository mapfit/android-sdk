package com.mapfit.mapfitsdk.utils

import android.content.Context
import android.content.Intent
import android.view.View

/**
 * Created by dogangulcan on 1/23/18.
 */


fun Context.startActivitySafe(intent: Intent) {
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun View.isVisible() = visibility == View.VISIBLE