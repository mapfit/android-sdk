package com.mapfit.mapfitsdk

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MAX_ZOOM
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MIN_ZOOM
import com.mapfit.mapfitsdk.geometry.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
class MapViewTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()
    private lateinit var mapView: com.mapfit.mapfitsdk.MapView
    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMapClickListener: OnMapClickListener

    @Mock
    private lateinit var onMapDoubleClickListener: OnMapDoubleClickListener

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        mapView = MapView(mMockContext, null)

        mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@MapViewTest.mapfitMap = mapfitMap
            }
        })
    }

    @Test
    fun testInitValuesExistence() {
        Assert.assertNotNull(mapView)
        Assert.assertNotNull(mapView.mapfitMap)
        Assert.assertNotNull(mapView.layers)
    }

    @Test
    @UiThreadTest
    fun testDefaultValues() {
        Assert.assertEquals(MapTheme.MAPFIT_DAY, mapfitMap.getMapOptions().mapTheme)
    }

    @Test
    @UiThreadTest
    fun testZoom() {
        val zoomLevel = 5f
        mapfitMap.setZoom(zoomLevel)
        Assert.assertEquals("Setting map zoom", zoomLevel, mapfitMap.getZoom())
    }

    @Test
    @UiThreadTest
    fun testOutOfBoundZoom() {
        val zoomLevel = 5f
        mapfitMap.setZoom(zoomLevel)

        val zoomLevelAboveMax = (MAP_MAX_ZOOM + 5).toFloat()
        mapfitMap.setZoom(zoomLevelAboveMax)

        // zoomLevelAboveMax should not be applied
        Assert.assertEquals(zoomLevel, mapfitMap.getZoom())

        val zoomLevelBelowMin = (MAP_MIN_ZOOM - 5).toFloat()
        mapfitMap.setZoom(zoomLevelBelowMin)

        // zoomLevelBelowMin should not be applied
        Assert.assertEquals(zoomLevel, mapfitMap.getZoom())
    }

    @Test
    @UiThreadTest
    fun testAddingRemovingLayers() {
        val layer = Layer()
        mapfitMap.addLayer(layer)
        Assert.assertEquals(layer, mapfitMap.getLayers().last())

        mapfitMap.removeLayer(layer)
        Assert.assertNotEquals(layer, mapfitMap.getLayers().last())
    }

    @Test
    @UiThreadTest
    fun testSettingCenter() {
        val latLng = LatLng(40.7441855, -73.995394)
        mapfitMap.setCenter(latLng)

        val actualLatLng = mapfitMap.getCenter()
        Assert.assertEquals(latLng.lat, actualLatLng.lat, 0.0000001)
        Assert.assertEquals(latLng.lon, actualLatLng.lon, 0.0000001)

    }

    @Test
    @UiThreadTest
    fun testMapClickListener() {
        mapfitMap.setOnMapClickListener(onMapClickListener)
        mapView.singleTapResponder().onSingleTapConfirmed(0f, 32f)
        verify(onMapClickListener, times(1))
                .onMapClicked(LatLng(89.840598043218, 157.50080354385057))
    }

    @Test
    @UiThreadTest
    fun testOnMapDoubleClickListener() {
        mapfitMap.setOnMapDoubleClickListener(onMapDoubleClickListener)
        mapView.doubleTapResponder()?.onDoubleTap(0f, 32f)
        verify(onMapDoubleClickListener, times(1))
                .onMapDoubleClicked(LatLng(89.840598043218, 157.50080354385057))
    }


}
