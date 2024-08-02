package com.example.weatherapplication.Model

import com.google.gson.annotations.SerializedName

data class ForecastResponseApi(
    @SerializedName("list") val list: List<ForecastItem>?
) {
    data class ForecastItem(
        @SerializedName("dt") val dt: Int?,
        @SerializedName("main") val main: Main?,
        @SerializedName("weather") val weather: List<Weather?>?,
        @SerializedName("dt_txt") val dtTxt: String?
    ) {
        data class Main(
            @SerializedName("temp") val temp: Double?,
            @SerializedName("humidity") val humidity : Int
        )

        data class Weather(
            @SerializedName("id") val id: Int?,
            @SerializedName("description") val description: String?
        )
    }
}
