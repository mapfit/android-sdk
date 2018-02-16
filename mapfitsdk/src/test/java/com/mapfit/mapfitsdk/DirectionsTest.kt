package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.directions.Directions
import com.mapfit.mapfitsdk.directions.DirectionsCallback
import com.mapfit.mapfitsdk.exceptions.MapfitConfigurationException
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
 * Created by dogangulcan on 2/4/18.
 */
class DirectionsTest {

    @Mock
    lateinit var directionsCallback: DirectionsCallback

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test(expected = MapfitConfigurationException::class)
    fun testMapfitConfiguration() {
        Directions().getRoute(
            originAddress = "119 W 24th St",
            destinationAddress = "107 Sacred Heart Ln",
            callback = directionsCallback
        )
    }

    @Test
    fun testErrorCallback() {

        Mapfit.getInstance(apiKey = "abc")

        val server = MockWebServer()
        server.url("api.mapfit.com/v2/directions")

        server.enqueue(MockResponse().setBody(mockResponse))
//        server.start()

        Directions().getRoute(
            originAddress = "119 W 24th St",
            destinationAddress = "107 Sacred Heart Ln",
            callback = directionsCallback
        )

        Thread.sleep(1000)

        verify(directionsCallback, times(1))
            .onError(
                ArgumentMatchers.anyString(),
                Mockito.any(Exception::class.java) ?: Exception()
            )

        server.shutdown()
    }

    private val mockResponse = ""
}