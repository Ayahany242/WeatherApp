package com.example.weather.model.repository

import com.example.weather.model.RemoteDataSource.RemoteDataSource
import com.example.weather.model.localDataSource.LocalDataSource
import com.example.weather.model.localDataSource.sharedPreferences.SharedPreferencesDataSource
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

private const val TAG = "WeatherResponse"
class RepositoryImp internal constructor(private val remoteDataSource: RemoteDataSource,
                                         private val localDataSource: LocalDataSource,
                                         private val sharedPreferencesDataSource: SharedPreferencesDataSource? = null) :
    Repository {
    companion object {
        @Volatile
        private var INSTANCE: RepositoryImp? = null
        fun getInstance(remote: RemoteDataSource,local:LocalDataSource,sharedPreferencesDataSource: SharedPreferencesDataSource? = null): RepositoryImp {
            return INSTANCE ?: synchronized(this) {
                val instance = RepositoryImp(
                    remote,
                    local,
                    sharedPreferencesDataSource = sharedPreferencesDataSource
                )
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
       // Log.i(TAG, "getCurrentWeather: at repository ")
        return remoteDataSource.getCurrentWeather(lat, lon, appid, units, lang)
    }

    override suspend fun getAllWeather(): Flow<List<WeatherDBModel>> {
        return localDataSource.getAllWeather()
    }
    override suspend fun insertWeather(weather: WeatherDBModel): Long {
        return localDataSource.insertCurrentWeather(weather)
    }
    override suspend fun deleteWeather(weather: WeatherDBModel): Int {
        return localDataSource.deleteCurrentWeather(weather)
    }

    override suspend fun getAllFavourites(): Flow<List<LocationData>> {
       return localDataSource.getAllFavourites()
    }

    override suspend fun insertFavourite(favouriteCity: LocationData) {
        localDataSource.insertFavourite(favouriteCity)
    }

    override suspend fun deleteFavourite(favouriteCity: LocationData) {
        localDataSource.deleteFavourite(favouriteCity)
    }

    override fun getData(key: String, defaultValue: String): String {
        return sharedPreferencesDataSource?.getData(key, defaultValue)!!
    }
    override fun saveData(key: String, value: String) {
        sharedPreferencesDataSource?.saveData(key, value)
    }

}
