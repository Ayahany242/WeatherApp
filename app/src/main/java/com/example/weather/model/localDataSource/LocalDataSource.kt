package com.example.weather.model.localDataSource

import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertCurrentWeather(weather: WeatherDBModel) : Long
    suspend fun deleteCurrentWeather(weather:WeatherDBModel) : Int
     fun getAllWeather(): Flow<List<WeatherDBModel>>
     fun getAllFavourites(): Flow<List<LocationData>>
    suspend fun insertFavourite(favouriteCity: LocationData)
    suspend fun deleteFavourite(favouriteCity: LocationData)
}