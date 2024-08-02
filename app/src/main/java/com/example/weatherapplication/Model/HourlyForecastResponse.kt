package com.example.weatherapplication.Model

data class HourlyForecastResponse(
    val list: List<HourlyForecastItem>? = null
)

data class HourlyForecastItem(
    val dt: Int? = null,
    val main: Main? = null,
    val weather: List<Weather>? = null
)

data class Main(
    val temp: Double? = null,
    val humidity: Double? = null // Add humidity if available
)

data class Weather(
    val id: Int? = null,
    val icon: String? = null
)
