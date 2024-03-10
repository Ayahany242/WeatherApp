package com.example.weather.model

import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

interface Repository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, appid: String, units: String, lang: String): Response<WeatherResponse>

}