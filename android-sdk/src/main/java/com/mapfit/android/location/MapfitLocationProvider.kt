package com.mapfit.android.location

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import com.mapfit.android.Mapfit
import com.mapfit.android.utils.logError

/**
 * Location API for accessing accurate device location.
 *
 * Created by dogangulcan on 3/1/18.
 */
@SuppressLint("MissingPermission")
internal class MapfitLocationProvider(context: Context) {

    var lastLocation: Location? = null
        private set

    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val listenerMap =
        HashMap<com.mapfit.android.location.LocationListener, LocationListener>()

    /**
     * This method starts listening location updates until [removeLocationUpdates] method is called.
     * You should call [removeLocationUpdates] when you don't need location updates anymore.
     *
     * @param locationRequest for request preferences
     * @param locationListener for location updates to be passed to
     */
    @JvmOverloads
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun requestLocationUpdates(
        locationRequest: LocationRequest = LocationRequest(),
        locationListener: com.mapfit.android.location.LocationListener
    ) {

        if (isLocationPermissionGranted()) {
            val locationCallback = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (isBetterLocation(location, lastLocation)) {
                        lastLocation = location
                    }

                    lastLocation?.let { locationListener.onLocation(it) }
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    val providerStatus = when (status) {
                        0 -> ProviderStatus.OUT_OF_SERVICE
                        1 -> ProviderStatus.TEMPORARILY_UNAVAILABLE
                        else -> ProviderStatus.ENABLED
                    }

                    locationListener.onProviderStatus(providerStatus)
                }

                override fun onProviderEnabled(provider: String) {
                    locationListener.onProviderStatus(ProviderStatus.ENABLED)
                }

                override fun onProviderDisabled(provider: String) {
                    locationListener.onProviderStatus(ProviderStatus.DISABLED)
                }
            }

            locationManager.requestLocationUpdates(
                locationRequest.interval,
                locationRequest.minimumDisplacement,
                getLocationCriteria(locationRequest.locationPriority),
                locationCallback,
                null
            )

            listenerMap[locationListener] = locationCallback
        } else {
            logError("Failed to request location. Location permission is not granted.")
        }
    }

    /**
     * Removes and disables location updates for given [LocationListener].
     *
     * @param locationListener
     */
    fun removeLocationUpdates(locationListener: com.mapfit.android.location.LocationListener) {
        locationManager.removeUpdates(listenerMap[locationListener])
        listenerMap.remove(locationListener)
    }

    /**
     * Builds [Criteria] for location requests.
     *
     * @return criteria
     */
    private fun getLocationCriteria(locationPriority: LocationPriority): Criteria {

        val (accuracy, power) = when (locationPriority) {
            LocationPriority.HIGH_ACCURACY -> Pair(Criteria.ACCURACY_HIGH, Criteria.POWER_HIGH)
            LocationPriority.LOW_ACCURACY -> Pair(Criteria.ACCURACY_LOW, Criteria.POWER_LOW)
        }

        return Criteria().apply {
            horizontalAccuracy = accuracy
            verticalAccuracy = accuracy
            powerRequirement = power
            isAltitudeRequired = false
            isSpeedRequired = false
            isCostAllowed = false
        }
    }

    private fun isGpsEnabled() =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isNetworkEnabled() =
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    internal fun isLocationPermissionGranted() =
        Mapfit.getContext()?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

}