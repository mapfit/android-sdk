package com.mapfit.android

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.annotations.Polyline
import com.mapfit.android.directions.DirectionsType
import com.mapfit.android.directions.model.Route
import com.mapfit.android.directions.model.Trip
import com.mapfit.android.geometry.LatLng
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Exception

/**
 * Created by dogangulcan on 2/5/18.
 */
@RunWith(AndroidJUnit4::class)
class DirectionsInstrumentationTest {

    private lateinit var mapfitMap: MapfitMap

    @Mock
    lateinit var routeDrawCallback: DirectionsOptions.RouteDrawCallback
//
    @Mock
    lateinit var route: Route

    private val context = InstrumentationRegistry.getContext()

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        MapView(context).getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@DirectionsInstrumentationTest.mapfitMap = mapfitMap
            }
        })

    }

    @Test
    @UiThreadTest
    fun testOnRouteAddedCallback() {

        mapfitMap.getDirectionsOptions()
            .setDestination(LatLng(40.744255, -73.993774))
            .setOrigin(LatLng(40.575534, -73.961857))
            .setType(DirectionsType.DRIVING)
            .showDirections(routeDrawCallback)

        Mockito.verify(routeDrawCallback, Mockito.times(1))
            .onRouteDrawn(
                Mockito.any(Route::class.java) ?:route,
                Mockito.any(List::class.java) as List<Polyline>
            )

        Assert.assertTrue(mapfitMap.getDirectionsOptions().routeDrawn)

    }

    @Test
    @UiThreadTest
    fun testOnRouteErrorCallback() {

        mapfitMap.getDirectionsOptions()
            .setDestination(LatLng())
            .setOrigin(LatLng())
            .setType(DirectionsType.DRIVING)
            .showDirections(routeDrawCallback)

        Mockito.verify(routeDrawCallback, Mockito.times(1))
            .onError(
                ArgumentMatchers.anyString(),
                Mockito.any(Exception::class.java) ?: Exception()
            )

    }

}