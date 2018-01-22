package com.mapfit

import com.mapfit.mapfitsdk.geocoder.GeocodeParser
import org.junit.Assert
import org.junit.Test

/**
 * Created by dogangulcan on 1/22/18.
 */
class GeocoderParserTest {

    @Test
    fun parseResponse() {
        val parser = GeocodeParser()
        val addressList = parser.parseGeocodeResponse(response)

        Assert.assertTrue(addressList.isNotEmpty())

        val firstAddress = addressList[0]

        Assert.assertTrue(firstAddress.streetAddress.isNotBlank())
        Assert.assertTrue(firstAddress.adminArea.isNotBlank())
        Assert.assertNotNull(firstAddress.status)
        Assert.assertTrue(firstAddress.locality.isNotBlank())
        Assert.assertTrue(firstAddress.neighborhood.isNotBlank())
        Assert.assertTrue(firstAddress.postalCode.isNotBlank())
        Assert.assertEquals(0.0,firstAddress.latitude,0.0001)
        Assert.assertEquals(0.0,firstAddress.longitude,0.0001)
        Assert.assertTrue(firstAddress.postalCode.isNotBlank())

        val entrance = firstAddress.entrances[0]

        Assert.assertNotNull(entrance.entranceType)
        Assert.assertEquals(40.74405, entrance.latitude, 0.0001)
        Assert.assertEquals(-73.99324, entrance.longitude, 0.0001)

    }

    val response = "[{ \"street_address\": \"119 W 24th St\", \"status-code\": 1, \"admin_1\": \"NY\", \"locality\": \"New York\", \"neighborhood\": \"chelsea\", \"entrances\": [{ \"place-type\": \"entrance of place\", \"lon\": -73.99324, \"lat\": 40.74405, \"entrance-type\": \"pedestrian-primary\" }], \"postal_code\": \"10011\" }]"
}