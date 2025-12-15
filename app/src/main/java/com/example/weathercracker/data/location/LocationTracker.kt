package com.example.weathercracker.data.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}