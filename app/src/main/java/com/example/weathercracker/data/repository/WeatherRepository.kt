package com.example.weathercracker.data.repository

import com.example.weathercracker.data.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherResponse>
    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse>
}