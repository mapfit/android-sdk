package com.mapfit.android.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.callback.OnMarkerClickListener
import com.mapfit.android.geometry.LatLng
import junit.framework.Assert
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Marker] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class MarkerTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMarkerClickListener: OnMarkerClickListener

    @Mock
    private lateinit var onPlaceInfoClickListener: MapfitMap.OnPlaceInfoClickListener

    @Mock
    private lateinit var placeInfoAdapter: MapfitMap.PlaceInfoAdapter

    private val latLng = LatLng(40.693825, -73.998691)

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<MapViewTestActivity> = ActivityTestRule(
        MapViewTestActivity::class.java,
        true,
        true
    )

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        Mapfit.getInstance(mMockContext, mMockContext.getString(R.string.mapfit_debug_api_key))
        val mapView: MapView = activityRule.activity.findViewById(R.id.mapView)

        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@MarkerTest.mapfitMap = mapfitMap
            }
        })
    }

    @After
    @UiThreadTest
    fun dispose() {
        Mapfit.dispose()
    }


    @Test
    @UiThreadTest
    fun testDefaults() {
        val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
        Assert.assertTrue(marker.visibility)
        Assert.assertTrue(marker.tag.isBlank())
        Assert.assertTrue(marker.title.isBlank())
        Assert.assertTrue(marker.subtitle1.isBlank())
        Assert.assertTrue(marker.subtitle2.isBlank())
        Assert.assertNull(marker.address)
        Assert.assertNotSame(0L, marker.id)
    }

    @Test
    @UiThreadTest
    fun testMarkerPosition() {
        // initial position
        val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
        Assert.assertEquals(latLng, marker.position)

        // valid position
        val latLng2 = LatLng(42.693825, -63.998691)
        marker.position = latLng2
        Assert.assertEquals(latLng2, marker.position)

        // invalid position
        val latLng3 = LatLng(412.693825, -653.998691)
        marker.position = latLng3
        Assert.assertEquals(latLng2, marker.position)
    }


    @Test
    @UiThreadTest
    fun testAddRemoveMarker() {
        val marker = mapfitMap.addMarker(MarkerOptions().position(LatLng()))
        Assert.assertNotNull(marker)

        mapfitMap.removeMarker(marker)

        Assert.assertTrue(marker.mapBindings.size == 0)
    }

    @Test
    @UiThreadTest
    fun testAddRemoveMarkerFromItself() {
        val marker = mapfitMap.addMarker(MarkerOptions().position(LatLng()))
        Assert.assertNotNull(marker)

        Layer().add(marker)
        Assert.assertTrue(marker.mapBindings.size == 1)
        Assert.assertTrue(marker.layers.size == 1)

        marker.remove()

        Assert.assertTrue(marker.mapBindings.size == 0)
        Assert.assertTrue(marker.layers.size == 0)
    }

    @Test
    fun testAddMarkerWithAddress() {
        runBlocking {
            delay(500)

            val expectedMarker =
                mapfitMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            40.74405,
                            -73.99324
                        )
                    )
                )

            var actualMarker: Marker? = null

            mapfitMap.addMarker(
                MarkerOptions().streetAddress(
                    "119 w 24th st new york ny 10011",
                    true
                ).addBuildingPolygon(true),
                object : OnMarkerAddedCallback {
                    override fun onMarkerAdded(marker: Marker) {
                        actualMarker = marker
                    }

                    override fun onError(exception: Exception) {

                    }
                })

            delay(1500)
            Assert.assertEquals(
                expectedMarker.position.lat,
                actualMarker?.position?.lat ?: 0.0,
                0.0001
            )
            Assert.assertEquals(
                expectedMarker.position.lng,
                actualMarker?.position?.lng
                        ?: 0.0,
                0.0001
            )

            // check building polygon existence
            Assert.assertNotNull(actualMarker?.buildingPolygon)
        }
    }

    @Test
    fun testMarkerClickListener() {
        runBlocking {
            delay(500)

            mapfitMap.setCenter(latLng)
            delay(500)

            mapfitMap.setOnMarkerClickListener(onMarkerClickListener)
            val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
            clickOnMarker(marker)

            verify(onMarkerClickListener, times(1)).onMarkerClicked(marker)
        }
    }

    @Test
    fun testPlaceInfoOpenClose() = runBlocking {
        delay(500)

        val marker = mapfitMap.addMarker(MarkerOptions().position(latLng).title("title"))

        clickOnMarker(marker)

        Assert.assertEquals(1, marker.placeInfoMap.size)

        val placeInfo = marker.placeInfoMap.values.first()
        Assert.assertTrue(placeInfo!!.getVisibility())

        val map = marker.mapBindings.keys.first()
        val screenPosition = marker.getScreenPosition(map)
        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt() + 50))
        delay(500)

        Assert.assertTrue(marker.placeInfoMap.values.isEmpty())
    }

    @Test
    fun testDefaultPlaceInfoClickListener() {
        runBlocking {
            delay(500)

            val marker = mapfitMap.addMarker(MarkerOptions().position(latLng).title("title"))

            mapfitMap.setOnPlaceInfoClickListener(onPlaceInfoClickListener)

            clickOnMarker(marker)
            clickOnPlaceInfo(marker)

            verify(onPlaceInfoClickListener, times(1)).onPlaceInfoClicked(marker)
        }
    }

    @Test
    fun testPlaceInfoAdapter() {
        runBlocking {
            delay(500)

            val marker = mapfitMap.addMarker(MarkerOptions().position(latLng).title("title"))

            mapfitMap.setPlaceInfoAdapter(placeInfoAdapter)

            clickOnMarker(marker)

            verify(placeInfoAdapter, times(1)).getPlaceInfoView(marker)
        }
    }

    private fun clickOnMarker(marker: Marker) = runBlocking {
        delay(500)
        val map = marker.mapBindings.keys.first()
        val screenPosition = marker.getScreenPosition(map)
        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt() - 30))
        delay(1500)
    }

    private fun clickOnPlaceInfo(marker: Marker) = runBlocking {
        val screenPosition = marker.getScreenPosition(marker.mapBindings.keys.first())
        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt() - 250))
        delay(1500)
    }


}

