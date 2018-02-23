package com.mapfit.mapfitdemo.ui.coffeeshop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapTheme
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.geometry.LatLng
import com.mapfit.mapfitdemo.R
import kotlinx.android.synthetic.main.activity_showcase.*

class ShowcaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_showcase)

        map.getMapAsync(MapTheme.MAPFIT_DAY, onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
//                val boundsBuilder = LatLngBounds.Builder()
//                boundsBuilder.include(LatLng(40.744043, -73.993209))
//                boundsBuilder.include(LatLng(40.6902223, -73.9770368))
//                boundsBuilder.include(LatLng(40.7061326, -74.000769))
//
//                val bounds = boundsBuilder.build()
//                val paddingPercentage = 0.5f
//
//                mapfitMap.setLatLngBounds(bounds, paddingPercentage)

                drawPolygon(mapfitMap)

            }
        })

    }

    fun drawPolygon(mapfitMap: MapfitMap) {
        val polygon = mapfitMap.addPolygon(
            listOf(
                listOf(
                    LatLng(40.7368876593604, -73.9785409464787),
                    LatLng(40.7313501345546, -73.982556292978),
                    LatLng(40.7344347901369, -73.9899029597005),
                    LatLng(40.7354082589172, -73.9898742137078),
                    LatLng(40.7433247184166, -73.9840748526015),
                    LatLng(40.7419697680819, -73.9808596541241),
                    LatLng(40.739498439778, -73.9826675979102),
                    LatLng(40.7375539536562, -73.9780522649762),
                    LatLng(40.7368876593604, -73.9785409464787)
                )
            )
        )

        mapfitMap.setLatLngBounds(polygon.getLatLngBounds(), 1f)

    }

}

