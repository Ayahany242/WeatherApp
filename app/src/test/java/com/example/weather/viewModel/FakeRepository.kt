package com.example.weather.viewModel

import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.repository.Repository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class FakeRepository :Repository{
    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        appid: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeather(): Flow<List<WeatherDBModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(weather: WeatherDBModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weather: WeatherDBModel): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavourites(): Flow<List<LocationData>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavourite(favouriteCity: LocationData) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavourite(favouriteCity: LocationData) {
        TODO("Not yet implemented")
    }

}