package com.example.weather.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.Repository
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.utils.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

private const val TAG = "WeatherResponse"
class AppViewModel(private val repository : Repository): ViewModel(){
    private var _weather: MutableStateFlow<ApiStatus> = MutableStateFlow(ApiStatus.Loading)
    var weather : StateFlow<ApiStatus> = _weather

    private var _allWeatherFromRoom: MutableStateFlow<ApiStatus> = MutableStateFlow(ApiStatus.Loading)
    val allWeatherFromRoom: StateFlow<ApiStatus> = _allWeatherFromRoom

    private var _allFavourites:MutableStateFlow<List<LocationData>?> = MutableStateFlow(null)
    val allFavourites:StateFlow<List<LocationData>?> = _allFavourites

    init {
        getAllWeatherFromRoom()
        getAllFavourite()
    }

    // Get user's current location and weather
    fun fetchWeather(lat:Float, lon:Float,lang:String ,apiKey:String,units: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getCurrentWeather(lat, lon, apiKey, units, lang)
                if (response.isSuccessful) {
                    if(response.body() != null){
                        _weather.value = ApiStatus.Success(response.body() as WeatherResponse)
                    }
                    Log.i(TAG, "fetchWeather: ${response.body()}")
                } else {
                    Log.i(TAG, "fetchWeather: \"Failed to fetch weather data\")")
                    _weather.value = ApiStatus.Error("Failed to fetch weather data:  ${
                        response.message()
                    }")
                }
            }catch (e: Exception) {
                _weather.value = ApiStatus.Error("Failed to fetch weather data: ${e.message}")
            }
        }
    }
    fun insertWeatherInRoom(weather:WeatherDBModel) {
        viewModelScope.launch(Dispatchers.IO){
            try {
               val result = repository.insertWeather(weather)
                if (result>0){
                    Log.i(TAG, "insertWeatherInRoom: insertWeatherInRoom result = $result")
                    _weather.value = ApiStatus.Success("Added Successfully")
                }
                else{
                    _weather.value = ApiStatus.Error("Failed to added")
                }
            }catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _weather.value = ApiStatus.Error(e.message.toString())
            }
        }
    }

    fun deleteWeatherFromRoom(weather:WeatherDBModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.deleteWeather(weather)
                if (result>0){
                    Log.i(TAG, "deleteWeatherFromRoom: deleteWeatherFromRoom result = $result")
                    _weather.value = ApiStatus.Success("deleted Successfully")
                }
                else{
                    _weather.value = ApiStatus.Error("Failed to delete")
                }
            }catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _weather.value = ApiStatus.Error(e.message.toString())
            }
        }
    }
    fun getAllWeatherFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllWeather()
                .catch {
                    _allWeatherFromRoom.value = ApiStatus.Error(it.message.toString())
                }
                .collect{weatherList->
                    _allWeatherFromRoom.value = ApiStatus.Success(weatherList)
                }
        }
    }

    fun insertFavourite(favoriteModel: LocationData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertFavourite(favoriteModel)
            } catch (e: Exception) {
                Timber.tag("FavouriteViewModel").e(e.message.toString())
            }
        }
    }

    fun deleteFavourite(favouritModel: LocationData) {
        viewModelScope.launch(Dispatchers.IO){
            try {
               repository.deleteFavourite(favouritModel)
            } catch (e: Exception) {
                Timber.tag("FavouriteViewModel").e(e.message.toString())

            }
        }
    }
    fun getAllFavourite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllFavourites()
                    .catch {

                    }
                    .collect{
                        _allFavourites.value = it
                    }

            } catch (e: Exception) {
                Log.e("FavouriteViewModel", e.message.toString())
            }
        }
    }

    fun saveData(key: String, value: String) {
        repository.saveData(key, value)
    }

    fun getData(key: String, defaultValue: String): String {
        return repository.getData(key, defaultValue)
    }
}