package com.example.weather.model.localDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM Weather_Table")
    fun getAllWeather(): Flow<List<WeatherDBModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherDBModel) :Long
    @Delete
    suspend fun deleteWeather(weather: WeatherDBModel) : Int

    @Query("SELECT * FROM FAVOURITE_CITIES")
     fun getAllFavourites(): Flow<List<LocationData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favouriteCity: LocationData)

    @Delete
    suspend fun deleteFavourite(favouriteCity: LocationData)
}