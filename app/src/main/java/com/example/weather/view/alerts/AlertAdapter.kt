package com.example.weather.view.alerts


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.AlarmItemBinding

class AlertsAdapter(private val removeClickListener: RemoveClickListener) : ListAdapter<AlertPojo, AlertsAdapter.ViewHolder>(DiffUtils) {
    class ViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alertWeatherData = getItem(position)

        holder.binding.imageViewDelete.setOnClickListener{
            removeClickListener.onRemoveClick(alertWeatherData)
        }

        holder.binding.textViewEndDate.setDate(alertWeatherData.end)
        holder.binding.textViewEndTime.setTime(alertWeatherData.end)

    }



    object DiffUtils : DiffUtil.ItemCallback<AlertPojo>() {
        override fun areItemsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem == newItem
        }

    }

    class RemoveClickListener(val removeClickListener : (AlertPojo) -> Unit){
        fun onRemoveClick(alertEntity: AlertPojo) = removeClickListener(alertEntity)
    }
}