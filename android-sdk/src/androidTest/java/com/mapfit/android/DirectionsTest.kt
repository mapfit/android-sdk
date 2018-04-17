package com.mapfit.android

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.DirectionsType
import com.mapfit.android.directions.model.Route
import com.mapfit.android.exceptions.MapfitConfigurationException
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
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
    @UiThreadTest
    fun testMapfitConfiguration() {
        val originAddress = "111 Macdougal Street new york ny"
        val destinationAddress = "119 W 24th Street new york ny"

        Directions().route(
            originAddress = originAddress,
            destinationAddress = destinationAddress,
            callback = directionsCallback
        )
    }

    @Test
    fun testLatLngDirections() = runBlocking {
        instantiateMapfit(this@DirectionsTest.context)

        val originAddress = "111 Macdougal Street new york ny"
        val destinationAddress = "119 W 24th Street new york ny"

        Directions().route(
            originAddress = originAddress,
            destinationAddress = destinationAddress,
            callback = directionsCallback
        )
        delay(1500)

        Mockito.verify(directionsCallback, Mockito.times(1))
            .onSuccess(ArgumentMatchers.any(Route::class.java) ?: Route())
    }

    @Test
    fun testErrorDirections() = runBlocking {
        instantiateMapfit(this@DirectionsTest.context)

        Directions().route(
            directionsType = DirectionsType.DRIVING,
            callback = directionsCallback
        )

        delay(1500)

        Mockito.verify(directionsCallback, Mockito.times(1))
            .onError(
                ArgumentMatchers.anyString(),
                Mockito.any(Exception::class.java) ?: Exception()
            )
    }

}