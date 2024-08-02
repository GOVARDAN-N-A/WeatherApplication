package com.example.weatherapplication.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapplication.Repository.WeatherRepository
import com.example.weatherapplication.Server.ApiClient

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    constructor() : this(WeatherRepository(ApiClient.getClient().create(com.example.weatherapplication.ApiService::class.java)))

    fun loadingCurrentWeather(lat: Double, lon: Double, unit: String) =
        repository.getCurrentWeather(lat, lon, unit)

    fun getCoordinates(cityName: String, limit: Int, apiKey: String) =
        repository.getCoordinates(cityName, limit, apiKey)

    fun get5DayForecast(lat: Double, lon: Double, unit: String, apiKey: String) =
        repository.get5DayForecast(lat, lon, unit, apiKey)

    fun getHourlyForecast(lat: Double, lon: Double, unit: String, apiKey: String) =
        repository.getHourlyForecast(lat, lon, unit, apiKey)

}
