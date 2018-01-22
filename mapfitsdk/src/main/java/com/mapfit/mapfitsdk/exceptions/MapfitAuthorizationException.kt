package com.mapfit.mapfitsdk.exceptions

/**
 * Created by dogangulcan on 1/18/18.
 */
class MapfitAuthorizationException : RuntimeException(
        "\nMapfit API key is not authorized." +
                "\nPlease visit https://mapfit.com/getakey to get a new API key.\n"
)