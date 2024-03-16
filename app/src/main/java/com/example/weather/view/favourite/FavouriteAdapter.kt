package com.example.weather.view.favourite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.FavItemRowBinding
import com.example.weather.model.pojo.LocationData

private const val TAG = "FavouriteAdapter"
class FavouriteAdapter(private var deleteClickListener:(LocationData)-> Unit,private var cardClickListener:(LocationData)-> Unit):  ListAdapter<LocationData, FavouriteAdapter.ViewHolder>(
    DaysDiffUnit()
){
    private lateinit var rowBinding: FavItemRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rowBinding = FavItemRowBinding.inflate(inflater,parent,false)
        return ViewHolder(rowBinding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        Log.i(TAG, "onBindViewHolder: DaysAdapter $position")
        holder.cityTv.text = currentItem.cityName
        holder.deleteCity.setOnClickListener {
            deleteClickListener(currentItem)
        }
        holder.onCardClick.setOnClickListener {
            cardClickListener(currentItem)
        }
    }
    class ViewHolder(binding: FavItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val cityTv:TextView = binding.favCityTv
        val deleteCity :ImageView = binding.deleteIcon
        val onCardClick : CardView = binding.favCityCardView
    }
}

class DaysDiffUnit: DiffUtil.ItemCallback<LocationData>(){
    override fun areItemsTheSame(oldItem: LocationData, newItem: LocationData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: LocationData, newItem: LocationData): Boolean {
        return oldItem == newItem
    }
}
