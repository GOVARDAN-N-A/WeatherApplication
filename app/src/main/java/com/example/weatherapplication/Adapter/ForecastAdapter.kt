import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.Model.HourlyForecastItem
import com.example.weatherapplication.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForecastAdapter(private val forecastList: List<HourlyForecastItem>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private val limitedForecastList = forecastList.take(10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = limitedForecastList[position]
        holder.timeTextView.text = forecast.dt?.let { formatTime(it) }
        holder.tempTextView.text = "${forecast.main?.temp?.toInt().toString()}° C"
        holder.humidityTextView.text = "${forecast.main?.humidity?.toInt() ?: "N/A"}%"

        // Get the weather code from the API
        val weatherCode = forecast.weather?.get(0)?.id
        // Map the weather code to the drawable resource
        val iconResId = getWeatherIconResId(holder.itemView.context, weatherCode)

        if (iconResId != 0) {
            holder.weatherIcon.setImageResource(iconResId)
        } else {
            holder.weatherIcon.setImageResource(R.drawable.clear_image) // Default icon
        }
    }

    override fun getItemCount(): Int = limitedForecastList.size

    private fun formatTime(timestamp: Int): String {
        val sdf = SimpleDateFormat("h a", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        return sdf.format(date)
    }

    private fun getWeatherIconResId(context: Context, weatherCode: Int?): Int {
        return when (weatherCode) {
            in 200..232 -> R.drawable.storm_image
            in 300..321 -> R.drawable.drizzle_image
            in 500..531 -> R.drawable.rainy_image
            in 600..622 -> R.drawable.snow_image
            in 701..781 -> R.drawable.foggy_image
            800 -> R.drawable.clear_image
            in 801..804 -> R.drawable.cloudy_image
            else -> R.drawable.clear_image // Default icon
        }
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeText)
        val tempTextView: TextView = itemView.findViewById(R.id.tempText)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
        val humidityTextView: TextView = itemView.findViewById(R.id.humidityText)
    }
}
