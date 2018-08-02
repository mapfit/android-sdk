package com.mapfit.android.anim

import android.animation.TypeEvaluator
import com.mapfit.android.geometry.LatLng


class RouteEvaluator : TypeEvaluator<LatLng> {
    override fun evaluate(t: Float, startPoint: LatLng, endPoint: LatLng): LatLng {
        val lat = startPoint.lat + t * (endPoint.lat - startPoint.lat)
        val lng = startPoint.lng + t * (endPoint.lng - startPoint.lng)
        return LatLng(lat, lng)
    }
}
