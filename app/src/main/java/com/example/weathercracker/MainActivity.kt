package com.example.weathercracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.weathercracker.ui.theme.WeatherCrackerTheme
import com.example.weathercracker.ui.weather.WeatherScreen
import com.example.weathercracker.ui.weather.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherCrackerTheme {
                WeatherScreen(viewModel = weatherViewModel)
            }
        }
    }
}