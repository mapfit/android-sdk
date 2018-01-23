package com.mapfit.mapfitsdk

import android.content.Context
import com.mapfit.mapfitsdk.exceptions.MapfitAuthorizationException
import com.mapfit.mapfitsdk.exceptions.MapfitConfigurationException
import junit.framework.Assert
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

    }

    @Test(expected = MapfitConfigurationException::class)
    fun testNoApiKey() {
        Assert.assertEquals("nokey", Mapfit.getApiKey())
    }

    @Test
    fun testApiKey() {
        Mapfit.getInstance(context, "somerandomapikey")
        Assert.assertEquals("somerandomapikey", Mapfit.getApiKey())
    }

}