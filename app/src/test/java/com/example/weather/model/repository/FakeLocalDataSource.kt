package com.example.weather.model.repository

import com.example.weather.model.localDataSource.LocalDataSource
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalDataSource:LocalDataSource {
    private val weatherList = mutableListOf<WeatherDBModel>()
    private val weatherFlow = MutableStateFlow<List<WeatherDBModel>>(emptyList())

    private val favouritesList = mutableListOf<LocationData>()
    private val favouritesFlow = MutableStateFlow<List<LocationData>>(emptyList())

    override suspend fun insertCurrentWeather(weather: WeatherDBModel): Long {
        weatherList.add(weather)
        updateWeather()
        return 1
    }

    override suspend fun deleteCurrentWeather(weather: WeatherDBModel): Int {
        if (weatherList.isEmpty())
            return 0
        weatherList.remove(weather)
        updateWeather()
        return 1
    }

    override fun getAllWeather(): Flow<List<WeatherDBModel>> {
        return weatherFlow
    }


    override fun getAllFavourites(): Flow<List<LocationData>> {
        return favouritesFlow
    }

    override suspend fun insertFavourite(favouriteCity: LocationData) {
        favouritesList.add(favouriteCity)
        updateFavourites()
    }

    override suspend fun deleteFavourite(favouriteCity: LocationData) {
        if (favouritesList.isNotEmpty()){
            favouritesList.remove(favouriteCity)
            updateFavourites()
        }
    }

    private fun updateWeather() {
        weatherFlow.value = weatherList.toList()
    }

    private fun updateFavourites() {
        favouritesFlow.value = favouritesList.toList()
    }
}