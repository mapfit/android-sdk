package com.mapfit.android

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.exceptions.MapfitConfigurationException
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
import com.mapfit.android.geocoder.model.Address
import com.mapfit.android.geometry.LatLng
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.lang.Exception


/**
 * Created by dogangulcan on 1/22/18.
 */
@RunWith(AndroidJUnit4::class)
class GeocoderTest {

    @Mock
    lateinit var geocoderCallback: GeocoderCallback

    private val context = InstrumentationRegistry.getContext()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun dispose() {
        Mapfit.dispose()
    }

    @Test(expected = MapfitConfigurationException::class)
    fun testMapfitConfiguration() {
        Geocoder().geocode(
            "119 w 24th st new york ny 10011",
            true,
            geocoderCallback
        )
    }

    @Test(expected = MapfitConfigurationException::class)
    fun testMapfitConfiguration2() {
        Geocoder().reverseGeocode(
            LatLng(),
            true,
            geocoderCallback
        )
    }

    @Test
    fun testGeocoderSuccessCallback() {

        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        Geocoder().geocode(
            "119 w 24th st new york ny 10011",
            true,
            geocoderCallback
        )

        Thread.sleep(1000)
        verify(geocoderCallback, times(1))
            .onSuccess(
                Mockito.any(List::class.java) as List<Address>? ?: listOf()
            )
    }


    @Test
    fun testGeocoderErrorCallback() {

        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        Geocoder().geocode(
            "",
            true,
            geocoderCallback
        )

        Thread.sleep(1000)
        verify(geocoderCallback, times(1))
            .onError(
                ArgumentMatchers.anyString(),
                Mockito.any(Exception::class.java) ?: Exception()
            )
    }


    @Test
    fun testReverseGeocoderSuccessCallback() {
        Mapfit.getInstance(context, context.getString(R.string.mapfit_debug_api_key))

        Geocoder().reverseGeocode(
            LatLng(40.74405, -73.99324),
            true,
            geocoderCallback
        )

        Thread.sleep(1000)
        verify(geocoderCallback, times(1))
            .onSuccess(
                Mockito.any(List::class.java) as List<Address>? ?: listOf()
            )
    }

}