// HourlyForecastResponse.kt
package com.example.weather

data class HourlyForecastResponse(
    val city: City,
    val list: List<Hourly>
) {
    data class City(
        val name: String,
        val coord: Coord,
        val country: String
    )

    data class Hourly(
        val dt: Int,
        val main: Main,
        val weather: List<Weather>,
        val dt_txt: String
    )
}
