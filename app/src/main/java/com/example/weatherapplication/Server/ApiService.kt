package com.example.weatherapplication

import com.example.weatherapplication.Model.CurrentResponseApi
import com.example.weatherapplication.Model.GeocodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<CurrentResponseApi>

    @GET("geo/1.0/direct")
    fun getCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Call<List<GeocodeResponse>>


}
