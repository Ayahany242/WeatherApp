package com.example.weather.view.alerts.alertViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.Repository

class AlertViewModelFactory(private val weatherRepo: Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
        {
            AlertViewModel(weatherRepo) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}