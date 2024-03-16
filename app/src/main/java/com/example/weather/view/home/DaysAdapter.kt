package com.example.weather.view.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.DaysRowItemBinding
import com.example.weather.model.pojo.DailyItem
import com.example.weather.utils.AppIcons
import com.example.weather.utils.ConvertUnits
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "DaysAdapter"
class DaysAdapter(private var tempUnit:String):  ListAdapter<DailyItem, DaysAdapter.ViewHolder>(
    DaysDiffUnit()
){
    private lateinit var rowBinding: DaysRowItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rowBinding = DaysRowItemBinding.inflate(inflater,parent,false)
        return ViewHolder(rowBinding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.i(TAG, "onBindViewHolder: DaysAdapter ${position}")

        currentItem.let { response ->
            Log.i(TAG, "onBindViewHolder: DaysAdapter ${currentItem.dt?.let { getDay(currentItem.dt) }}")
            holder.dayTV.text = currentItem.dt?.let { getDay(currentItem.dt) }
           // holder.temp.text = response.temp?.day?.let { ConvertUnits.convertTemp(it, tempUnit = tempUnit) }
            val tempMin = response.temp?.min?.let {
                ConvertUnits.convertTemp(it, tempUnit = tempUnit)
            }
            val tempMax = response.temp?.max?.let {
                ConvertUnits.convertTemp(it, tempUnit = tempUnit)
            }
            holder.temp.text = "$tempMin / $tempMax"
            holder.tempStatu.text = response.weather?.get(0)?.description
            var iconForHourName =response?.weather?.get(0)?.icon.toString()
            AppIcons.getIcon(iconForHourName, holder.iconWeather)
        }
    }


    class ViewHolder(binding: DaysRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val dayTV: TextView = binding.hourTime
        val iconWeather:ImageView = binding.imageView3
        val tempStatu:TextView = binding.tempStatu
        val temp :TextView = binding.tempTime
    }
}

class DaysDiffUnit: DiffUtil.ItemCallback<DailyItem>(){
    override fun areItemsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem == newItem
    }
}

fun getDay(dt: Int): String {
    val cityTxtFormat = SimpleDateFormat("EEEE")
    val cityTxtData = Date(dt.toLong() * 1000)
    return cityTxtFormat.format(cityTxtData)
}