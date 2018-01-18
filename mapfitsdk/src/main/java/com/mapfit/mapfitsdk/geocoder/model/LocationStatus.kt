package com.mapfit.mapfitsdk.geocoder.model

/**
 * Created by dogangulcan on 1/18/18.
 */
enum class LocationStatus(var code: Int) {
    ERROR(0),
    SUCCESS(1),
    INTERPOLATED(2),
    ZERO_RESULTS(3),
    ENTRANCE(4),
    FALLBACK(13)
}


//0	Error
//1	Success (entrance)
//2	Interpolated
//3	Zero results
//4	Entrance type substituted
//13	fallback