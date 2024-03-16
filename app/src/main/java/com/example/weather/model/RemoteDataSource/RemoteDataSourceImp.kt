package com.example.weather.model.RemoteDataSource

import android.util.Log
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response
private const val TAG = "WeatherResponse"
class RemoteDataSourceImp private constructor(): RemoteDataSource {
    private val retrofit : Services = RetrofitClient.services

    companion object{
        private var INSTANCE: RemoteDataSourceImp? = null
        fun getInstance(): RemoteDataSourceImp {
            return INSTANCE ?: synchronized(this) {
                val instance = RemoteDataSourceImp()
                INSTANCE = instance
                instance
            }
        }
    }
    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        appid: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
       return retrofit.getCurrentTempData(lat,lon,appid,units,lang)
    }
}