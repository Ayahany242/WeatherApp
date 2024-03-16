package com.example.weather.view.home

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.HourlyWeatherRowBinding
import com.example.weather.model.pojo.HourlyItem
import com.example.weather.utils.AppIcons
import com.example.weather.utils.ConvertUnits
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TAG = "HourlyAdapter"
class HourlyAdapter ( var tempUnit:String, var timezone: String):  ListAdapter<HourlyItem, HourlyAdapter.ViewHolder>(
    HourlyDiffUnit()
){
    lateinit var rowBinding: HourlyWeatherRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rowBinding = HourlyWeatherRowBinding.inflate(inflater,parent,false)
        return ViewHolder(rowBinding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.i(TAG, "onBindViewHolder: HourlyAdapter ${currentItem.dt}")
        currentItem.let { response ->
            holder.hourTV.text = currentItem.dt?.let { getCurrentTime(it,timezone) }
            holder.temp.text = response.temp?.let { ConvertUnits.convertTemp(it, tempUnit = tempUnit) }
            var iconForHourName =response?.weather?.get(0)?.icon.toString()
            AppIcons.getIcon(iconForHourName, holder.iconWeather)
        }
    }
    class ViewHolder( binding: HourlyWeatherRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val hourTV: TextView = binding.hourTime
        val iconWeather:ImageView = binding.imageView3
        val temp :TextView = binding.tempTime
    }
}

class HourlyDiffUnit: DiffUtil.ItemCallback<HourlyItem>(){
    override fun areItemsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem == newItem
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTime(dt: Int, timezone: String, format: String = "K:mm a"): String {

    val zoneId = ZoneId.of(timezone)
    val instant = Instant.ofEpochSecond(dt.toLong())
    val formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    return instant.atZone(zoneId).format(formatter)
}