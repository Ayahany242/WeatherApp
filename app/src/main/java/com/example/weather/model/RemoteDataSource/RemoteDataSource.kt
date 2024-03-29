package com.example.weather.model.RemoteDataSource

import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Float, lon: Float, appid: String, units: String, lang: String): Response<WeatherResponse>
}