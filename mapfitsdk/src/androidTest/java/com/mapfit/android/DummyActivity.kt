package com.mapfit.android

import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.os.Bundle
import android.support.annotation.Nullable


/**
 * Dummy activity for testing [MapView].
 *
 * Created by dogangulcan on 1/26/18.
 */
class DummyActivity : AppCompatActivity() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapView = MapView(this)
        mapView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mapView.id = R.id.mapView
        setContentView(mapView)
    }


}