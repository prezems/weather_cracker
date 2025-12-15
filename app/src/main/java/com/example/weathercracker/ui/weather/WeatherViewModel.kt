package com.example.weathercracker.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathercracker.data.location.LocationTracker
import com.example.weathercracker.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    val result = repository.getWeatherByLocation(location.latitude, location.longitude)
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _uiState.value = WeatherUiState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _uiState.value = WeatherUiState.Error(
                                exception.message ?: "Failed to fetch weather data"
                            )
                        }
                    )
                } else {
                    _uiState.value = WeatherUiState.Error("Could not determine location. Please ensure location permissions are granted.")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun loadWeatherForCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val result = repository.getWeatherByCity(cityName)
                result.fold(
                    onSuccess = { weatherResponse ->
                        _uiState.value = WeatherUiState.Success(weatherResponse)
                    },
                    onFailure = { exception ->
                        _uiState.value = WeatherUiState.Error(
                            exception.message ?: "Failed to fetch weather for $cityName"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}