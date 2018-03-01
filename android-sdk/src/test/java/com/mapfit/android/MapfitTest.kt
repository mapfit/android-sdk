package com.mapfit.android

import android.content.Context
import com.mapfit.android.exceptions.MapfitConfigurationException
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by dogangulcan on 1/19/18.
 */
class MapfitTest {

    @Mock
    lateinit var context: Context

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        Mapfit.dispose()
    }

    @After
    fun dispose() {
        Mapfit.dispose()
    }

    @Test(expected = MapfitConfigurationException::class)
    fun testNoApiKey() {
        Mapfit.getApiKey()
    }

    @Test
    fun testApiKey() {
        Mapfit.getInstance(context, "somerandomapikey")
        Assert.assertEquals("somerandomapikey", Mapfit.getApiKey())
    }

}