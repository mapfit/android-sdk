package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.exceptions.MapfitAuthorizationException
import com.mapfit.mapfitsdk.geocoder.Geocoder
import com.mapfit.mapfitsdk.geocoder.GeocoderCallback
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


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

    @Test
    fun testAPIResponse() {
//        val server = MockWebServer()
//
//        server.enqueue(MockResponse().setBody(mockResponse))
//        server.start()
//
//        val geocoder = Geocoder()
//        geocoder.geocodeAddress("119 w 24th st new york ny 10011", geocoderCallback)
//
//        val request = server.takeRequest()
//
//        Thread.sleep(1000)
//
//        Mockito.verify(geocoderCallback, Mockito.times(1))
//                .onError("Key not authorised", MapfitAuthorizationException())
//
//        server.shutdown()
    }


    val mockResponse = "[{\n" +
            "      \"street_address\": \"119 W 24th St\",\n" +
            "      \"status-code\": 1,\n" +
            "      \"admin_1\": \"NY\",\n" +
            "      \"locality\": \"New York\",\n" +
            "      \"neighborhood\": \"\",\n" +
            "      \"entrances\": [{\n" +
            "           \"place-type\": \"entrance of place\",\n" +
            "           \"lon\": -73.99324,\n" +
            "           \"lat\": 40.74405,\n" +
            "           \"entrance-type\": \"pedestrian-primary\"\n" +
            "      }],\n" +
            "      \"postal_code\": \"10011\"\n" +
            " }]"
}