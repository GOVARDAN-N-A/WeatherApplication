package com.example.weatherapplication.Repository

import com.example.weatherapplication.ApiService
import com.example.weatherapplication.Model.CurrentResponseApi
import com.example.weatherapplication.Model.ForecastResponseApi
import com.example.weatherapplication.Model.GeocodeResponse
import com.example.weatherapplication.Model.HourlyForecastResponse
import retrofit2.Call

class WeatherRepository(private val api: ApiService) {
    private val apiKey = "4b03a2bc72bbb54c777ad25fd395a272"

    fun getCurrentWeather(lat: Double, lon: Double, unit: String): Call<CurrentResponseApi> {
        return api.getCurrentWeather(lat, lon, unit, apiKey)
    }

    fun getCoordinates(cityName: String, limit: Int, apiKey: String): Call<List<GeocodeResponse>> {
        return api.getCoordinates(cityName, limit, apiKey)
    }

    fun get5DayForecast(lat: Double, lon: Double, unit: String, apiKey: String): Call<ForecastResponseApi> {
        return api.get5DayForecast(lat, lon, unit, apiKey)
    }

    fun getHourlyForecast(lat: Double, lon: Double, unit: String, apiKey: String): Call<HourlyForecastResponse>{
        return api.getHourlyForecast(lat, lon, unit, apiKey)
    }



}
