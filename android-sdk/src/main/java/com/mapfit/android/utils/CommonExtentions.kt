package com.mapfit.android.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable


/**
 * Created by dogangulcan on 1/23/18.
 */

internal fun Context.startActivitySafe(intent: Intent) {
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

/**
 * Returns rotated
 */
internal fun Bitmap.rotate(angle: Float): Bitmap {
    val w = width
    val h = height

    var newW = w
    var newH = h
    if (angle == 90f || angle == 270f) {
        newW = h
        newH = w
    }
    val rotatedBitmap = Bitmap.createBitmap(newW, newH, config)
    val canvas = Canvas(rotatedBitmap)

    val rect = Rect(0, 0, newW, newH)
    val matrix = Matrix()
    val px = rect.exactCenterX()
    val py = rect.exactCenterY()
    matrix.postTranslate(-width / 2f, -height / 2f)
    matrix.postRotate(angle)
    matrix.postTranslate(px, py)
    canvas.drawBitmap(
        this,
        matrix,
        Paint(ANTI_ALIAS_FLAG or DITHER_FLAG or FILTER_BITMAP_FLAG)
    )
    matrix.reset()

    return rotatedBitmap
}

/**
 * Converts drawable to bitmap.
 *
 * @return bitmap relative to screen density
 */
internal fun Drawable.toBitmap(context: Context): Bitmap {
    val density = context.resources.displayMetrics.densityDpi
    val bitmapDrawable = this as BitmapDrawable
    bitmapDrawable.setTargetDensity(density)
    val bitmap = bitmapDrawable.bitmap
    bitmap.density = density
    return bitmap
}