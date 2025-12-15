package com.example.weathercracker.data.repository

import com.example.weathercracker.data.api.WeatherApi
import com.example.weathercracker.data.model.WeatherResponse
import com.example.weathercracker.util.Constants
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = api.getWeatherByLocation(
                lat = lat,
                lon = lon,
                apiKey = Constants.API_KEY,
                units = Constants.METRIC_UNITS
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> {
        return try {
            val response = api.getWeatherByCity(
                cityName = cityName,
                apiKey = Constants.API_KEY,
                units = Constants.METRIC_UNITS
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}