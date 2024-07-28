package com.example.weatherapplication.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.Model.CurrentResponseApi
import com.example.weatherapplication.R
import com.example.weatherapplication.ViewModel.WeatherViewModel
import com.example.weatherapplication.databinding.ActivityWeatherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = 30.5
        val lon = 50.5
        val city = "trichy"
        binding.tvLocation.text = city

        weatherViewModel.loadingCurrentWeather(lat, lon, "metric").enqueue(object :
            Callback<CurrentResponseApi> {
            override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("WeatherActivity", "API Response: $data")
                    data?.let {
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
        Log.d("WeatherActivity", "Updating UI with data: $data")
        binding.temperatureText.text = "${data.main?.temp}° C"
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

        // Log the weather condition
        data.weather?.get(0)?.let {
            Log.d("WeatherActivity", "Weather condition: ${it.main}")
            updateWeatherGif(it.main ?: "")
        }
    }

    private fun updateWeatherGif(weatherCondition: String) {
        Log.d("WeatherActivity", "Updating weather GIF for condition: $weatherCondition")
        when (weatherCondition) {
            "Rain" -> {
                binding.climateGif.setImageResource(R.drawable.rainy_trans_gif)
                binding.root.setBackgroundResource(R.drawable.rainy_bg)
            }
            "Clear" -> {
                binding.climateGif.setImageResource(R.drawable.sunny_trans_gif)
                binding.root.setBackgroundResource(R.drawable.sunny_bg)
            }
            "Clouds" -> {
                binding.climateGif.setImageResource(R.drawable.rainy_trans_gif)
                binding.root.setBackgroundResource(R.drawable.cloudy_bg)
            }
            "Snow" -> {
                binding.climateGif.setImageResource(R.drawable.snow_bg)
                binding.root.setBackgroundResource(R.drawable.snow_bg)
            }
            "Wind" -> {
                binding.climateGif.setImageResource(R.drawable.rainy_trans_gif)
                binding.root.setBackgroundResource(R.drawable.snow_bg)
            }
            "Drizzle" -> {
                binding.climateGif.setImageResource(R.drawable.drizzle)
                binding.root.setBackgroundResource(R.drawable.drizzle_bg)
            }
            "Thunderstorm" -> {
                binding.climateGif.setImageResource(R.drawable.tunderstorm)
                binding.root.setBackgroundResource(R.drawable.snow_bg)
            }
            else -> {
                binding.climateGif.setImageResource(R.drawable.drizzle)
                binding.root.setBackgroundResource(R.drawable.default_bg)
            }
        }
    }
}
