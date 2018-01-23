package com.mapfit.mapfitsdk.exceptions

/**
 * Created by dogangulcan on 1/18/18.
 */
class MapfitConfigurationException : RuntimeException(
        "\nUsing MapView requires setting a valid access token. Use Mapfit.getInstance(context, apiKey)" +
                " to setup Mapfit." +
                "\nIf you don't have an api key, you can get from https://mapfit.com/getakey.\n"
)