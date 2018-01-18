package com.mapfit.mapfitsdk

import junit.framework.Assert
import org.junit.Test

/**
 * Unit tests for [Layer].
 *
 * Created by dogangulcan on 1/5/18.
 */
class LayerTest {

    @Test
    fun testLayerDefaultVisibility() {
        val layer = Layer()
        Assert.assertTrue(layer.isVisible)
    }

}