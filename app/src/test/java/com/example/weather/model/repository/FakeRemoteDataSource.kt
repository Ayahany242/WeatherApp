package com.example.weather.model.repository

import com.example.weather.model.RemoteDataSource.RemoteDataSource
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

class FakeRemoteDataSource :RemoteDataSource{
    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        appid: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        val weatherResponse = WeatherResponse(
            alerts = null,
            current = null,
            timezone = null,
            timezoneOffset = null,
            daily = null,
            hourly = null,
            minutely = null,
            lat = lat.toDouble(),
            lon = lon.toDouble()
        )
        return Response.success(weatherResponse)

    }
}