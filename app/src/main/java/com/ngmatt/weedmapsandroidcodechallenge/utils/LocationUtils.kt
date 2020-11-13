package com.ngmatt.weedmapsandroidcodechallenge.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LATITUDE
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LOCATION_MINIMUM_DISTANCE
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LOCATION_UPDATE_MILLIS
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LONGITUDE

/**
 * LocationUtils provides a simple API for starting, stopping and requesting location data.
 */
object LocationUtils {

    private var locationManager: LocationManager? = null
    private val networkProviderListener = NetworkProviderListener()
    private val gpsProviderListener = GpsProviderListener()
    private var lastKnownLocationGps: Location? = null
    private var lastKnownLocationNetwork: Location ? = null

    /**
     * NetworkProviderListener is a location listener that will report when a network location has
     * changed. This value will take priority over GPS, since GPS can be spotty and sometimes not
     * available at all.
     */
    class NetworkProviderListener: LocationListener {
        override fun onLocationChanged(location: Location) {
            lastKnownLocationNetwork = location
        }
    }

    /**
     * GPSProviderListener is a location listener that will report for GPS location changed. This
     * value can be spotty so we'll take this location second in priority to Network.
     */
    class GpsProviderListener: LocationListener {
        override fun onLocationChanged(location: Location) {
            lastKnownLocationGps = location
        }
    }

    /**
     * Whether or not the [Manifest.permission.ACCESS_FINE_LOCATION] permission is granted.
     * @param context: The application context.
     * @returns Whether or not the location permission is granted
     */
    fun isLocationPermissionGranted(context: Context) =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /**
     * Start tracking location via GPS and Network providers.
     * @param context: The application context.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        if (isLocationPermissionGranted(context)) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, DEFAULT_LOCATION_UPDATE_MILLIS, DEFAULT_LOCATION_MINIMUM_DISTANCE, networkProviderListener)
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, DEFAULT_LOCATION_UPDATE_MILLIS, DEFAULT_LOCATION_MINIMUM_DISTANCE, gpsProviderListener)
        }
    }

    /**
     * Stop location updates for GPS and Network.
     * @param context: The application context.
     */
    fun stopLocationUpdates(context: Context) {
        if (isLocationPermissionGranted(context)) {
            locationManager?.removeUpdates(networkProviderListener)
            locationManager?.removeUpdates(gpsProviderListener)
        }
    }

    /**
     * Get the last known location from the Network or GPS provider, in that order. If both are
     * missing, return the default latitude and longitude .
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context): Pair<Double, Double>? {
        return if (isLocationPermissionGranted(context)) {
            // Update the Network and GPS locations and return a pair of those values.
            if (lastKnownLocationNetwork == null)
                lastKnownLocationNetwork = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnownLocationGps == null)
                lastKnownLocationGps = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Pair(
                lastKnownLocationNetwork?.latitude ?: lastKnownLocationGps?.latitude ?: DEFAULT_LATITUDE,
                lastKnownLocationNetwork?.longitude ?: lastKnownLocationGps?.longitude ?: DEFAULT_LONGITUDE)
        } else {
            null
        }
    }

}