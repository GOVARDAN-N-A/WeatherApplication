package com.example.weatherapplication.Activity

import ForecastAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.LoginActivity
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
import java.util.Locale

import android.content.SharedPreferences

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.weatherapplication.ForecastAdapter
import com.example.weatherapplication.Model.HourlyForecastResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val apiKey = "4b03a2bc72bbb54c777ad25fd395a272"
    private lateinit var city: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerViewForecast)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        forecastAdapter = ForecastAdapter(emptyList())
        recyclerView.adapter = forecastAdapter



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.locationIcon.setOnClickListener {
            getLocation()
        }

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog(sharedPreferences)
        }

        city = sharedPreferences.getString("city", "chennai") ?: "chennai"
        Log.d("city", "user city $city")

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.cityNotFoundLayout.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
                if (query != null) {
                    city = query
                    if (isNetworkAvailable(this@WeatherActivity)) {
                        binding.noInternetLayout.visibility = View.GONE
                        binding.contentLayout.visibility = View.VISIBLE
                        fetchCoordinates(city)
                        binding.searchView.setQuery("", false)
                        binding.searchView.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                    } else {
                        showNoInternetError()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        if (isNetworkAvailable(this)) {
            binding.noInternetLayout.visibility = View.GONE
            binding.contentLayout.visibility = View.VISIBLE
            fetchCoordinates(city)
        } else {
            showNoInternetError()
        }
    }
    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            showLocationSettingsAlert()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currLat = location.latitude
                    val currLon = location.longitude
                    fetchWeather(currLat, currLon)
                    Log.d("loc", "loc $currLat, $currLon")
                } else {
                    requestNewLocationData()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationError", "Failed to get location", exception)
                requestNewLocationData()
            }
    }

    private fun requestNewLocationData() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                val currLat = location.latitude
                val currLon = location.longitude
                fetchWeather(currLat, currLon)
                Log.d("loc", "loc $currLat, $currLon")
            } else {
                Log.e("LocationError", "Location is null after requesting new data")
            }
        }
    }

    private fun showLocationSettingsAlert() {
        AlertDialog.Builder(this)
            .setTitle("Enable Location Services")
            .setMessage("Location services are required for this app. Please enable them in the settings.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showNoInternetError() {
        Log.d("NoInternet", "No internet connection")
        binding.backgroundImageView.setImageResource(R.drawable.auth_bg)
        binding.contentLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
//        binding.cityNotFoundLayout.visibility = View.VISIBLE
        binding.noInternetLayout.visibility = View.VISIBLE // Assuming you have this layout

        val retryButton = findViewById<Button>(R.id.tryAgainButton) // Make sure you have a retry button in noInternetLayout
        retryButton.setOnClickListener {
            if (isNetworkAvailable(this)) {
                fetchCoordinates(city)
                binding.noInternetLayout.visibility = View.GONE

            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showLogoutConfirmationDialog(sharedPreferences: SharedPreferences) {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", false)
                    apply()
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun fetchCoordinates(city: String) {
        binding.loadingLayout.visibility = View.VISIBLE
        weatherViewModel.getCoordinates(city, 1, apiKey).enqueue(object : Callback<List<GeocodeResponse>> {
            override fun onResponse(call: Call<List<GeocodeResponse>>, response: Response<List<GeocodeResponse>>) {
                Log.d("res code", "Response code: ${response.code()}")
                val responseBody = response.body()

                Log.d("res body", "Response body: $responseBody")

                if (response.isSuccessful) {
                    binding.loadingLayout.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    if (responseBody.isNullOrEmpty()) {
                        Log.e("WeatherActivity", "City not found: Empty response body")
                        Toast.makeText(this@WeatherActivity, "City not found", Toast.LENGTH_SHORT).show()
                        showCityNotFoundError()
                    } else {
                        val geocodeResponse = responseBody.firstOrNull()
                        if (geocodeResponse != null) {
                            binding.loadingLayout.visibility = View.GONE
                            val lat = geocodeResponse.lat ?: 0.0
                            val lon = geocodeResponse.lon ?: 0.0
                            fetchWeather(lat, lon)
                            fetch5DayForecast(lat, lon, apiKey)
                            fetchHourlyForecast(lat,lon,apiKey)
                        } else {
                            Log.e("WeatherActivity", "City not found: No valid geocode response")
                            showCityNotFoundError()
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("WeatherActivity", "Failed to fetch coordinates: ${response.code()} - $errorMessage")
                    Toast.makeText(this@WeatherActivity, "Failed to get coordinates", Toast.LENGTH_SHORT).show()
                    showCityNotFoundError()
                }
            }

            override fun onFailure(call: Call<List<GeocodeResponse>>, t: Throwable) {
                Log.e("WeatherActivity", "Failure: ${t.message}")
                Toast.makeText(this@WeatherActivity, "Failed to get coordinates", Toast.LENGTH_SHORT).show()
                showCityNotFoundError()
            }
        })
    }

    private fun showCityNotFoundError() {
        Log.d("not_Found", "Called")
        binding.backgroundImageView.setImageResource(R.drawable.auth_bg)
        binding.contentLayout.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.cityNotFoundLayout.visibility = View.VISIBLE
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        Log.d("fetchWeather", "$lat, $lon")
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
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(timestamp * 1000L)
            sdf.format(date)
        } else {
            "N/A"
        }
    }

    private fun updateUI(data: CurrentResponseApi) {
        Log.d("full_data", "data $data")
        binding.tvLocation.text = data.name
        binding.temperatureText.text = "${data.main?.temp?.roundToInt()}° C"
        binding.realFeelText.text = "Real Feel: ${data.main?.feelsLike}° C"
        binding.minTemperatureText.text = "${data.main?.tempMin?.roundToInt()}° C"
        binding.maxTemperatureText.text = "${data.main?.tempMax?.roundToInt()}° C"
        binding.latValue.text = String.format("%.2f", data.coord?.lat ?: 0.0)
        binding.lonValue.text = String.format("%.2f", data.coord?.lon ?: 0.0)
        binding.humidityValue.text = "${data.main?.humidity}%"
        binding.pressureValue.text = "${data.main?.pressure} hPa"
        binding.windSpeedText.text = "${data.wind?.speed} km/h"
        binding.sunriseTime.text = formatTime(data.sys?.sunrise)
        binding.sunsetTime.text = formatTime(data.sys?.sunset)

        data.wind?.deg?.let {
            val windDirection = data.wind.deg
            Log.d("Wind Direction", "Wd $windDirection")
            updateWindDirection(windDirection)
        }

        data.weather?.get(0)?.let {
            val weatherCode = it.id
            val weatherDescription = it.description
            Log.d("code", "showing code  $weatherCode $weatherDescription")
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
            in 293..337 -> binding.windDirectionImage.setImageResource(R.drawable.north_west)
            else -> binding.windDirectionImage.setImageResource(R.drawable.south_east)
        }
    }

    private fun updateWeatherGif(weatherCode: Int, weatherDescription: String) {
        Log.d("WeatherUpdate", "Weather Code: $weatherCode, Description: $weatherDescription")
        binding.statusText.text = weatherDescription
        when (weatherCode) {
            in 200..232 -> { // Thunderstorm
                binding.climateGif.setImageResource(R.drawable.storm_image)
                binding.backgroundImageView.setImageResource(R.drawable.storm_bg)
            }
            in 300..321 -> { // Drizzle
                binding.climateGif.setImageResource(R.drawable.drizzle_image)
                binding.backgroundImageView.setImageResource(R.drawable.drizzle_bg)
            }
            in 500..531 -> { // Rain
                binding.climateGif.setImageResource(R.drawable.rainy_image)
                binding.backgroundImageView.setImageResource(R.drawable.rainy_bg)
            }
            in 600..622 -> { // Snow
                binding.climateGif.setImageResource(R.drawable.snow_image)
                binding.backgroundImageView.setImageResource(R.drawable.snow_bg)
            }
            in 701..781 -> { // Atmosphere
                binding.climateGif.setImageResource(R.drawable.foggy_image)
                binding.backgroundImageView.setImageResource(R.drawable.fog_bg)
            }
            800 -> { // Clear
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                if (hour in 6..18) { // Morning (6 AM to 6 PM)
                    binding.climateGif.setImageResource(R.drawable.clear_image)
                    binding.backgroundImageView.setImageResource(R.drawable.mrng_clear_bg)
                } else { // Night
                    binding.climateGif.setImageResource(R.drawable.clear_image)
                    binding.backgroundImageView.setImageResource(R.drawable.night_clear_bg)
                }
            }
            in 801..804 -> { // Clouds
                binding.climateGif.setImageResource(R.drawable.cloudy_image)
                binding.backgroundImageView.setImageResource(R.drawable.cloudy_bg)
            }
            else -> {
                binding.climateGif.setImageResource(R.drawable.clear_image)
                binding.backgroundImageView.setImageResource(R.drawable.mrng_clear_bg)
            }
        }
    }

    private fun fetch5DayForecast(lat: Double, lon: Double, apiKey: String) {
        weatherViewModel.get5DayForecast(lat, lon, "metric", apiKey).enqueue(object : Callback<ForecastResponseApi> {
            override fun onResponse(call: Call<ForecastResponseApi>, response: Response<ForecastResponseApi>) {
                if (response.isSuccessful) {
                    val forecastList = response.body()?.list ?: return

                    val processedForecastList = forecastList.map { forecastItem ->
                        val roundedTemp = forecastItem.main?.temp?.let { temp ->
                            String.format("%.1f", temp)
                        }

                        forecastItem.copy(main = forecastItem.main?.copy(temp = roundedTemp?.toDouble()))
                    }

                    updateForecastUI(processedForecastList)
                } else {
                    Toast.makeText(this@WeatherActivity, "Failed to get forecast data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                Toast.makeText(this@WeatherActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun fetchHourlyForecast(lat: Double, lon: Double, apiKey: String) {
        weatherViewModel.getHourlyForecast(lat, lon, "metric", apiKey).enqueue(object : Callback<HourlyForecastResponse> {
            override fun onResponse(
                call: Call<HourlyForecastResponse>,
                response: Response<HourlyForecastResponse>
            ) {
                if (response.isSuccessful) {
                    val hourlyForecastValue = response.body()?.list ?: emptyList()
                    Log.d("HourlyForecast", "Data: $hourlyForecastValue")
                    forecastAdapter = ForecastAdapter(hourlyForecastValue)
                    recyclerView.adapter = forecastAdapter
                } else {
                    Log.e("HourlyForecast", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HourlyForecastResponse>, t: Throwable) {
                Log.e("HourlyForecast", "Failure: ${t.message}")
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

        val tempTextViews = listOf(
            R.id.day1_temp,
            R.id.day2_temp,
            R.id.day3_temp,
            R.id.day4_temp,
            R.id.day5_temp
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
                val temp = forecastItems[0].main?.temp?.let { String.format("%.1f", it) } ?: "N/A"
                val forecastDesc = forecastItems[0].weather?.get(0)?.description ?:0

                val imageViewId = weatherImages[index]
                val contentDescId = contentDesc[index]
                val dateTextViewId = dayOfWeekTextViews[index]
                val tempTextViewId = tempTextViews[index]

                val imageView = findViewById<ImageView>(imageViewId)
                val descTextView = findViewById<TextView>(contentDescId)
                val dateTextView = findViewById<TextView>(dateTextViewId)
                val tempTextView = findViewById<TextView>(tempTextViewId)

                updateWeatherImage(imageView, weatherCode, descTextView, forecastDesc.toString())
                dateTextView.text = formattedDate
                tempTextView.text = "$temp°C"
            } else {
                // Handle case where there is no forecast data for the day
                val dateTextViewId = dayOfWeekTextViews[index]
                val tempTextViewId = tempTextViews[index]

                val dateTextView = findViewById<TextView>(dateTextViewId)
                val tempTextView = findViewById<TextView>(tempTextViewId)

                dateTextView.text = "N/A"
                tempTextView.text = "N/A"
            }
        }
    }



    private fun updateWeatherImage(imageView: ImageView, weatherCode: Int, descTextView: TextView, forecastDesc: String ) {
        descTextView.text = forecastDesc
        when (weatherCode) {
            in 200..232 -> { // Thunderstorm
                imageView.setImageResource(R.drawable.storm_image)
//                descTextView.text = "Thunderstorm"
            }
            in 300..321 -> { // Drizzle
                imageView.setImageResource(R.drawable.drizzle_image)
//                descTextView.text = "Drizzle"
            }
            in 500..531 -> { // Rain
                imageView.setImageResource(R.drawable.rainy_image)
//                descTextView.text = "Rain"
            }
            in 600..622 -> { // Snow
                imageView.setImageResource(R.drawable.snow_image)
//                descTextView.text = "Snow"
            }
            in 701..781 -> { // Atmosphere
                imageView.setImageResource(R.drawable.foggy_image)
//                descTextView.text = "Fog"
            }
            800 -> { // Clear
                imageView.setImageResource(R.drawable.clear_image)
//                descTextView.text = "Clear"
            }
            in 801..804 -> { // Clouds
                imageView.setImageResource(R.drawable.cloudy_image)
//                descTextView.text = "Clouds"
            }
            else -> {
                imageView.setImageResource(R.drawable.clear_image)
//                descTextView.text = "Clear"
            }
        }
    }
}