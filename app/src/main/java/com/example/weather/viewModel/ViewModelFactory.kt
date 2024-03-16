package com.example.weather.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.Repository


class ViewModelFactory(private val _repo: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AppViewModel::class.java)){
            AppViewModel(_repo) as T
        }else
            throw IllegalArgumentException("ViewModelClassNotFound")
    }
}
/*override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ViewModel::class.java)){
            ViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModelClassNotFound")
        }
    }*/