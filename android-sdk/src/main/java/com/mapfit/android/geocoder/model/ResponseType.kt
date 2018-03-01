package com.mapfit.android.geocoder.model

/**
 * Status code for API result.
 *
 * Created by dogangulcan on 1/18/18.
 */
enum class ResponseType(var code: Int) {
    ERROR(0),
    SUCCESS(1),
    INTERPOLATED(2),
    ZERO_RESULTS(3),
    ENTRANCE(4),
    FALLBACK(13)
}