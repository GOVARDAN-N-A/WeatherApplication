package com.example.weatherapplication.Activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.Model.CurrentResponseApi
import com.example.weatherapplication.Model.ForecastResponseApi
import com.example.weatherapplication.Model.GeocodeResponse
import com.example.weatherapplication.R
import com.example.weatherapplication.ViewModel.WeatherViewModel
import com.example.weatherapplication.databinding.ActivityWeatherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val apiKey = "4b03a2bc72bbb54c777ad25fd395a272"
    var city = "Trichy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    city = query
                    fetchCoordinates(city) // Call fetchCoordinates when the query is submitted
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        fetchCoordinates(city)
    }

    private fun fetchCoordinates(city: String) {
        weatherViewModel.getCoordinates(city, 1, apiKey).enqueue(object : Callback<List<GeocodeResponse>> {
            override fun onResponse(call: Call<List<GeocodeResponse>>, response: Response<List<GeocodeResponse>>) {
                if (response.isSuccessful) {
                    val geocodeResponse = response.body()?.firstOrNull()
                    geocodeResponse?.let {
                        val lat = it.lat ?: 0.0
                        val lon = it.lon ?: 0.0
                        fetchWeather(lat, lon)
                        fetch5DayForecast(lat, lon)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("WeatherActivity", "Failed to fetch coordinates: ${response.code()} - $errorMessage")
                    Toast.makeText(this@WeatherActivity, "Failed to get coordinates", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GeocodeResponse>>, t: Throwable) {
                Log.e("WeatherActivity", "Failure: ${t.message}")
                t.printStackTrace() // Print the full stack trace
                Toast.makeText(this@WeatherActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        weatherViewModel.loadingCurrentWeather(lat, lon, "metric").enqueue(object : Callback<CurrentResponseApi> {
            override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        Log.d("WeatherActivity", "Updating UI with data: $data")
                        updateUI(it)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("WeatherActivity", "Error: ${response.code()} - $errorMessage")
                    Toast.makeText(this@WeatherActivity, "Failed to get weather data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                Log.e("WeatherActivity", "Failure: ${t.message}")
                Toast.makeText(this@WeatherActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatTime(timestamp: Int?): String {
        return if (timestamp != null) {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val date = java.util.Date(timestamp * 1000L)
            sdf.format(date)
        } else {
            "N/A"
        }
    }

    private fun updateUI(data: CurrentResponseApi) {
        binding.tvLocation.text = city
        binding.temperatureText.text = "${data.main?.temp?.roundToInt()}° C"
        binding.realFeelText.text = "Real Feel: ${data.main?.feelsLike}° C"
        binding.minTemperatureText.text = "${data.main?.tempMin}° C"
        binding.maxTemperatureText.text = "${data.main?.tempMax}° C"
        binding.latValue.text = data.coord?.lat.toString()
        binding.lonValue.text = data.coord?.lon.toString()
        binding.humidityValue.text = "${data.main?.humidity}%"
        binding.pressureValue.text = "${data.main?.pressure} hPa"
        binding.windSpeedText.text = "${data.wind?.speed} km/h"
        binding.sunriseTime.text = formatTime(data.sys?.sunrise)
        binding.sunsetTime.text = formatTime(data.sys?.sunset)

        data.wind?.deg?.let{
            val windDirection = data.wind.deg
            Log.d("Wind Direction", "Wd $windDirection")
            updateWindDirection(windDirection)
        }

        // Update GIFs or images based on weather conditions
        data.weather?.get(0)?.let {
            val weatherCode = it.id
            val weatherDescription = it.description
            Log.d("code","showing code  $weatherCode $weatherDescription")
            if (weatherCode != null) {
                if (weatherDescription != null) {
                    updateWeatherGif(weatherCode, weatherDescription)
                }
            }
        }
    }

    private fun updateWindDirection(windDirection: Int) {

        when (windDirection) {
            in 0..22 -> binding.windDirectionImage.setImageResource(R.drawable.north)
            in 23..67 -> binding.windDirectionImage.setImageResource(R.drawable.north_east)
            in 68..112 -> binding.windDirectionImage.setImageResource(R.drawable.east)
            in 113..157 -> binding.windDirectionImage.setImageResource(R.drawable.south_east)
            in 158..202 -> binding.windDirectionImage.setImageResource(R.drawable.south)
            in 203..247 -> binding.windDirectionImage.setImageResource(R.drawable.south_west)
            in 248..292 -> binding.windDirectionImage.setImageResource(R.drawable.west)
            in 293..337 ->binding.windDirectionImage.setImageResource(R.drawable.north_west)
            else -> binding.windDirectionImage.setImageResource(R.drawable.south_east)
        }

    }

    private fun updateWeatherGif(weatherCode: Int, weatherDescription: String) {
        Log.d("WeatherUpdate", "Weather Code: $weatherCode, Description: $weatherDescription")
        when (weatherCode) {
            in 200..232 -> { // Thunderstorm
                binding.climateGif.setImageResource(R.drawable.storm_status)
                binding.backgroundImageView.setImageResource(R.drawable.storm_bg)
            }
            in 300..321 -> { // Drizzle
                binding.climateGif.setImageResource(R.drawable.drizzle_status)
                binding.backgroundImageView.setImageResource(R.drawable.drizzle_bg)
            }
            in 500..531 -> { // Rain
                binding.climateGif.setImageResource(R.drawable.rainy_status_2)
                binding.backgroundImageView.setImageResource(R.drawable.rainy_bg)
            }
            in 600..622 -> { // Snow
                binding.climateGif.setImageResource(R.drawable.snow_status)
                binding.backgroundImageView.setImageResource(R.drawable.snow_bg)
            }
            in 701..781 -> { // Atmosphere
                binding.climateGif.setImageResource(R.drawable.fog_mist_status)
                binding.backgroundImageView.setImageResource(R.drawable.fog_bg)
            }
            800 -> { // Clear
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                if (hour in 6..18) { // Morning (6 AM to 6 PM)
                    binding.climateGif.setImageResource(R.drawable.clear_status)
                    binding.backgroundImageView.setImageResource(R.drawable.mrng_clear_bg)
                } else { // Night
                    binding.climateGif.setImageResource(R.drawable.clear_status)
                    binding.backgroundImageView.setImageResource(R.drawable.night_clear_bg)
                }
            }
            in 801..804 -> { // Clouds
                binding.climateGif.setImageResource(R.drawable.cloud_image)
                binding.backgroundImageView.setImageResource(R.drawable.cloudy_bg)
            }
            else -> {
                binding.climateGif.setImageResource(R.drawable.clear_status)
                binding.backgroundImageView.setImageResource(R.drawable.night_clear_bg)
            }
        }
    }

    private fun fetch5DayForecast(lat: Double, lon: Double) {
        weatherViewModel.get5DayForecast(lat, lon, "metric").enqueue(object : Callback<ForecastResponseApi> {
            override fun onResponse(call: Call<ForecastResponseApi>, response: Response<ForecastResponseApi>) {
                if (response.isSuccessful) {
                    val forecastList = response.body()?.list ?: return
                    updateForecastUI(forecastList)
                } else {
                    Toast.makeText(this@WeatherActivity, "Failed to get forecast data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                Toast.makeText(this@WeatherActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateForecastUI(forecastList: List<ForecastResponseApi.ForecastItem>) {
        val weatherImages = listOf(
            R.id.day1_image,
            R.id.day2_image,
            R.id.day3_image,
            R.id.day4_image,
            R.id.day5_image
        )

        val contentDesc = listOf(
            R.id.day1_desc,
            R.id.day2_desc,
            R.id.day3_desc,
            R.id.day4_desc,
            R.id.day5_desc
        )

        val dayOfWeekTextViews = listOf(
            R.id.day1_date,
            R.id.day2_date,
            R.id.day3_date,
            R.id.day4_date,
            R.id.day5_date
        )

        val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        // Get the current date
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Create a map to group forecasts by date
        val forecastsByDate = forecastList.groupBy {
            val date = it.dt?.let { dt -> Date(dt * 1000L) } ?: Date()
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        }


        // Iterate through the next 5 days and update the UI
        for (index in 0 until 5) {
            val targetDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, index)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val formattedDate = sdf.format(targetDate)
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(targetDate)

            val forecastItems = forecastsByDate[dateString] ?: emptyList()
            if (forecastItems.isNotEmpty()) {
                val weatherCode = forecastItems[0].weather?.get(0)?.id ?: 0
                val imageViewId = weatherImages[index]
                val contentDescId = contentDesc[index]
                val dateTextViewId = dayOfWeekTextViews[index]

                val imageView = findViewById<ImageView>(imageViewId)
                val descTextView = findViewById<TextView>(contentDescId)
                val dateTextView = findViewById<TextView>(dateTextViewId)

                updateWeatherImage(imageView, weatherCode, descTextView)
                dateTextView.text = formattedDate
            } else {
                // Handle case where there is no forecast data for the day
                val dateTextViewId = dayOfWeekTextViews[index]
                val dateTextView = findViewById<TextView>(dateTextViewId)
                dateTextView.text = "N/A"
            }
        }
    }



    private fun updateWeatherImage(imageView: ImageView, weatherCode: Int, descTextView: TextView) {
        when (weatherCode) {
            in 200..232 -> { // Thunderstorm
                imageView.setImageResource(R.drawable.storm_status)
                descTextView.text = "Thunderstorm"
            }
            in 300..321 -> { // Drizzle
                imageView.setImageResource(R.drawable.drizzle_status)
                descTextView.text = "Drizzle"
            }
            in 500..531 -> { // Rain
                imageView.setImageResource(R.drawable.rainy_status_2)
                descTextView.text = "Rain"
            }
            in 600..622 -> { // Snow
                imageView.setImageResource(R.drawable.snow_status)
                descTextView.text = "Snow"
            }
            in 701..781 -> { // Atmosphere
                imageView.setImageResource(R.drawable.fog_mist_status)
                descTextView.text = "Fog"
            }
            800 -> { // Clear
                imageView.setImageResource(R.drawable.clear_status)
                descTextView.text = "Clear"
            }
            in 801..804 -> { // Clouds
                imageView.setImageResource(R.drawable.cloud_image)
                descTextView.text = "Clouds"
            }
            else -> {
                imageView.setImageResource(R.drawable.clear_status)
                descTextView.text = "Clear"
            }
        }
    }
}
