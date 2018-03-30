package com.mapfit.android.location

/**
 * Represents location provider status.
 *
 * Created by dogangulcan on 3/2/18.
 */
enum class ProviderStatus {

    /**
     * States that the location provider is enabled and there will be location updates.
     */
    ENABLED,

    /**
     * States that the location provider is disabled and there won't be location updates.
     */
    DISABLED,

    /**
     * The provider is out of service.
     */
    OUT_OF_SERVICE,

    /**
     * The provider is expected to be available in near future.
     */
    TEMPORARILY_UNAVAILABLE,

}
