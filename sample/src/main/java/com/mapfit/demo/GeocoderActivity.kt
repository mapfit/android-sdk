package com.mapfit.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapTheme
import com.mapfit.android.MapView
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.annotations.MarkerOptions
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
import com.mapfit.android.geocoder.model.Address
import com.mapfit.android.geometry.LatLng
import com.mapfit.mapfitdemo.R

class GeocoderActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)

        mapView.getMapAsync(MapTheme.MAPFIT_DAY, object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap(mapfitMap)
            }
        })
    }

    private fun setupMap(mapfitMap: MapfitMap) {
        this.mapfitMap = mapfitMap

        mapfitMap.setCenter(LatLng(40.74405, -73.99324))
        mapfitMap.setZoom(14f)

        geocodeAddress()
        reverseGeocodeAddress()
    }


    /**
     * Geocodes an address.
     */
    private fun geocodeAddress() {
        Geocoder().geocode(
            "175 5th Ave, New York, NY 10010",
            true,
            object : GeocoderCallback {
                override fun onError(message: String, e: Exception) {}

                override fun onSuccess(addressList: List<Address>) {
                    var latLng = LatLng()
                    addressList.forEach { address ->
                        latLng =
                                LatLng(address.entrances.first().lat, address.entrances.first().lng)
                    }

                    val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
                    val polygon = mapfitMap.addPolygon(addressList[0].building.polygon)
                }
            })
    }

    /**
     * Reverse geocodes address of a [LatLng].
     */
    private fun reverseGeocodeAddress() {
        Geocoder().reverseGeocode(
            LatLng(40.74405, -73.99324),
            true,
            object : GeocoderCallback {
                override fun onError(message: String, e: Exception) {}

                override fun onSuccess(addressList: List<Address>) {
                    var latLng = LatLng()
                    addressList.forEach { address ->
                        latLng =
                                LatLng(address.entrances.first().lat, address.entrances.first().lng)
                    }

                    val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
                    val polygon = mapfitMap.addPolygon(addressList[0].building.polygon)
                }
            })
    }

}
