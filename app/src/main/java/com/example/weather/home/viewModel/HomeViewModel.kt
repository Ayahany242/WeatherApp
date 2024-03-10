package com.example.weather.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Repository
import com.example.weather.utils.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "WeatherResponse"
class HomeViewModel(private val repository : Repository): ViewModel(){
    private var _weather: MutableStateFlow<ApiStatus> = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    var weather : Flow<ApiStatus> = _weather

    fun fetchWeather(lat:Double, lon:Double,lang:String ,apiKey:String,units: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeather(lat, lon, apiKey, units, lang)
                if (response.isSuccessful) {
                    _weather.value = ApiStatus.Success(response.body())
                    Log.i(TAG, "fetchWeather: ${response.body()}")
                } else {
                    Log.i(TAG, "fetchWeather: \"Failed to fetch weather data\")")
                    _weather.value = ApiStatus.Error(Exception("Failed to fetch weather data"))
                }
            } catch (e: Exception) {
                _weather.value = ApiStatus.Error(Exception("Failed to fetch weather data at catch (e: Exception)"))
                Log.i(TAG, "fetchWeather: catch (e: Exception) ${e.message.toString()}")
            }
        }
    }
}