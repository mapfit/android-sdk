package com.mapfit.android

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.DirectionsType
import com.mapfit.android.directions.model.Route
import com.mapfit.android.exceptions.MapfitConfigurationException
import com.mapfit.android.geometry.LatLng
import org.junit.After
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
class DirectionsTest {

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var directionsCallback: DirectionsCallback

    private val context = InstrumentationRegistry.getContext()

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)
        Mapfit.dispose()
    }

    @After
    fun dispose() {
        Mapfit.dispose()
    }


    @Test(expected = MapfitConfigurationException::class)
    fun testMapfitConfiguration() {
        Directions().route(
            LatLng(40.744255, -73.993774),
            LatLng(40.575534, -73.961857),
            DirectionsType.DRIVING,
            directionsCallback
        )
    }

    @Test
    fun testLatLngDirections() {
        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        Directions().route(
            LatLng(40.744255, -73.993774),
            LatLng(40.575534, -73.961857),
            DirectionsType.DRIVING,
            directionsCallback
        )

        Thread.sleep(500)
        Mockito.verify(directionsCallback, Mockito.times(1))
            .onSuccess(ArgumentMatchers.any(Route::class.java) ?: Route())
    }

    @Test
    fun testErrorDirections() {
        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        Directions().route(
            directionsType = DirectionsType.DRIVING,
            callback = directionsCallback
        )

        Thread.sleep(500)
        Mockito.verify(directionsCallback, Mockito.times(1))
            .onError(
                ArgumentMatchers.anyString(),
                Mockito.any(Exception::class.java) ?: Exception()
            )
    }

}