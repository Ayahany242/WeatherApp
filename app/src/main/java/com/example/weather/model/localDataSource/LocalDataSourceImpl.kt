package com.example.weather.model.localDataSource

import android.content.Context
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl private constructor(ctx: Context):LocalDataSource{
    val dao: WeatherDao = WeatherDataBase.getInstance(ctx).getWeatherDao()
    companion object {
        @Volatile
        private var INSTANCE: LocalDataSourceImpl? = null
        fun getInstance(ctx: Context): LocalDataSourceImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = LocalDataSourceImpl(
                    ctx.applicationContext
                )
                INSTANCE = instance
                instance
            }
        }
    }
    override suspend fun insertCurrentWeather(weather: WeatherDBModel): Long {
       return dao.insertWeather(weather)
    }

    override suspend fun deleteCurrentWeather(weather: WeatherDBModel): Int {
       return dao.deleteWeather(weather)
    }

    override fun getAllWeather(): Flow<List<WeatherDBModel>> {
        return dao.getAllWeather()
    }

    override fun getAllFavourites(): Flow<List<LocationData>> {
        return dao.getAllFavourites()
    }

    override suspend fun insertFavourite(favouriteCity: LocationData) {
        dao.insertFavourite(favouriteCity)
    }

    override suspend fun deleteFavourite(favouriteCity: LocationData) {
        dao.deleteFavourite(favouriteCity)
    }

}