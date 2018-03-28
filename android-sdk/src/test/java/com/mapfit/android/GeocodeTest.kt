package com.mapfit.android

import com.mapfit.android.exceptions.MapfitConfigurationException
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
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
class GeocodeTest {

    @Mock
    lateinit var geocoderCallback: GeocoderCallback

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

    @Test
    fun testErrorCallback() {
        runBlocking {
            Mapfit.getInstance(apiKey = "abc")

            val geocoder = Geocoder()
            geocoder.geocode(
                "119 w 24th st new york ny 10011",
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
    }

}