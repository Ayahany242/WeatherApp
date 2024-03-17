package com.example.weather.model.repository

import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    suspend fun getCurrentWeather(lat: Float, lon: Float, appid: String, units: String, lang: String): Response<WeatherResponse>

    suspend fun getAllWeather(): Flow<List<WeatherDBModel>>
    suspend fun insertWeather(weather:WeatherDBModel):Long
    suspend fun deleteWeather(weather: WeatherDBModel):Int
    suspend fun getAllFavourites(): Flow<List<LocationData>>
    suspend fun insertFavourite(favouriteCity: LocationData)
    suspend fun deleteFavourite(favouriteCity: LocationData)
    fun getData(key: String, defaultValue: String): String
    fun saveData(key: String, value: String)
}