package com.mapfit.android

import com.mapfit.android.exceptions.MapfitConfigurationException
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
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

        Mapfit.getInstance(apiKey = "abc")

        val server = MockWebServer()
        server.url("api.mapfit.com/v2/geocode")

        server.enqueue(MockResponse().setBody(mockResponse))
//        server.start()

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


        server.shutdown()
    }

    private val mockResponse =
        "[{ \"locality\": \"New York\", \"postal_code\": \"10011\", \"admin_1\": \"NY\", \"country\" : \"USA\", \"neighborhood\": \"chelsea\", \"response_type\": 1, \"building\": { \"type\": \"Polygon\", \"coordinates\": [[[-73.992953, 40.744257], [-73.993265, 40.744389], [-73.993448, 40.744138], [-73.993136, 40.744006], [-73.992953, 40.744257]]] }, \"street_address\": \"119 W 24th St\", \"entrances\": [{ \"lon\": -73.99324, \"lat\": 40.74405, \"entrance_type\": \"pedestrian-primary\" }] }]"

}