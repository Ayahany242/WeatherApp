package com.example.weather.model.RemoteDataSource

import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Services {

    @GET("onecall")
    suspend fun getCurrentTempData(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response<WeatherResponse>
}