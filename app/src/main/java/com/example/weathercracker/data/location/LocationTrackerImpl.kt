package com.example.weathercracker.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationTrackerImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessCoarseLocationPermission && !hasAccessFineLocationPermission) {
            return null
        }

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            return null
        }

        // 1. Try to get the last known location first (faster)
        val lastLocation = suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result)
                    } else {
                        cont.resume(null)
                    }
                    return@apply
                }
                addOnSuccessListener { location ->
                    cont.resume(location)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }

        if (lastLocation != null) {
            return lastLocation
        }

        // 2. If last location is null, fetch the current location with a timeout
        return withTimeoutOrNull(10000L) { // 10 seconds timeout
            suspendCancellableCoroutine { cont ->
                val cancellationTokenSource = CancellationTokenSource()

                locationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).apply {
                    if (isComplete) {
                        if (isSuccessful) {
                            cont.resume(result)
                        } else {
                            cont.resume(null)
                        }
                        return@apply
                    }
                    addOnSuccessListener { location ->
                        cont.resume(location)
                    }
                    addOnFailureListener {
                        cont.resume(null)
                    }
                    addOnCanceledListener {
                        cont.cancel()
                    }
                }

                cont.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }
        }
    }
}