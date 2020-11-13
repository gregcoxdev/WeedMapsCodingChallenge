package com.ngmatt.weedmapsandroidcodechallenge.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LATITUDE
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LOCATION_MINIMUM_DISTANCE
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LOCATION_UPDATE_MILLIS
import com.ngmatt.weedmapsandroidcodechallenge.data.constants.DEFAULT_LONGITUDE

private const val TAG = "LocationUtils"

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
     * changed. This value will take priority over GPS, since GPS can be spoofed.
     */
    class NetworkProviderListener: LocationListener {

        override fun onLocationChanged(location: Location) {
            lastKnownLocationNetwork = location
            Log.i(TAG, "Network Location: $lastKnownLocationNetwork")
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * GPSProviderListener is a location listener that will report for GPS location changed. This
     * value can be easily spoofed, so we'll take Network location as a priority.
     */
    class GpsProviderListener: LocationListener {

        override fun onLocationChanged(location: Location) {
            lastKnownLocationGps = location
            Log.i(TAG, "GPS Location: $lastKnownLocationGps")
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
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
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
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
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager?.removeUpdates(networkProviderListener)
            locationManager?.removeUpdates(gpsProviderListener)
        }
    }

    /**
     * Get the last known location from the Network or GPS provider, in that order. If both are
     * missing, return null values which will be sent up and defaulted to [DEFAULT_LATITUDE] and
     * [DEFAULT_LONGITUDE].
     */
    fun getLastKnownLocation(context: Context): Pair<Double?, Double?> {
        return if (isLocationPermissionGranted(context)) {
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            val location = getLocationLatitudeLongitude(lastKnownLocationNetwork, LocationManager.NETWORK_PROVIDER)
                ?: getLocationLatitudeLongitude(lastKnownLocationGps, LocationManager.GPS_PROVIDER)
            Log.d("GREG", "Location to search not null: ${location?.latitude}, ${location?.longitude}")
            return Pair(location?.latitude, location?.longitude)
        } else {
            Pair(lastKnownLocationNetwork?.latitude, lastKnownLocationNetwork?.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationLatitudeLongitude(lastKnownLocation: Location?, provider: String): Location? {
        return lastKnownLocation ?: locationManager?.getLastKnownLocation(provider)
    }

}