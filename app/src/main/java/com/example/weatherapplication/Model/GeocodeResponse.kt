package com.example.weatherapplication.Model


import com.google.gson.annotations.SerializedName

data class GeocodeResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("country") val country: String?
)
