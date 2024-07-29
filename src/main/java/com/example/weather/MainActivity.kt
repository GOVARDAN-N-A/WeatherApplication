package com.example.weather

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("chennai")
        SearchCity()
    }
    private fun SearchCity() {
        val searchView =binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "17dea7edca8808ca56672ef5bf7f70e9", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity =responseBody.main.humidity
                    val windSpeed =responseBody.wind.speed
                    val sunrise =responseBody.sys.sunrise.toLong()
                    val sunset =responseBody.sys.sunset.toLong()
                    val sealevel =responseBody.main.pressure
                    val condition =responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp =responseBody.main.temp_max
                    val minTemp =responseBody.main.temp_min

                    binding.temp.text="$temperature °C"
                    binding.weather.text =condition
                    binding.maxTerm.text ="Max Temp :$maxTemp °C"
                    binding.minTerm.text ="Min Temp :$minTemp °C"
                    binding.Humidity.text ="$humidity %"
                    binding.windspeed.text ="$windSpeed m/s"
                    binding.sunrise.text ="${time(sunrise)}"
                    binding.sunset.text ="${time(sunset)}"
                    binding.sea.text ="$sealevel hpa"
                    binding.condition.text =condition
                    binding.day.text =dayName(System.currentTimeMillis())

                    binding.date.text = date()
                    binding.cityname.text ="$cityName"


                    // Log.d("TAG", "onResponse: $temperature")

                    changeImagsAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}", t)
            }
        })

    }

    private fun changeImagsAccordingToWeatherCondition(conditions:String) {
        when (conditions){
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
            "Partly Cloud","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
//            "Haze" ->{
//                binding.root.setBackgroundResource(R.drawable.white_cloud)
//                binding.lottieAnimationView.setAnimation(R.raw.cloud)
//
//            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp:Long): String {
        val sdf =SimpleDateFormat("HH:MM", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }


    fun dayName(timestamp:Long): String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
