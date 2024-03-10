package com.example.weather.model

import android.util.Log
import com.example.weather.model.RemoteDataSource.RemoteDataSource
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

private const val TAG = "WeatherResponse"
class RepositoryImp private constructor(private val remoteDataSource: RemoteDataSource) : Repository{
    companion object {
        @Volatile
        private var INSTANCE: RepositoryImp? = null
        fun getInstance(remote: RemoteDataSource): RepositoryImp {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryImp(
                   // local,
                    remote
                )
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        appid: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        Log.i(TAG, "getCurrentWeather: at repository ")
        return remoteDataSource.getCurrentWeather(lat, lon, appid, units, lang)
    }

}
