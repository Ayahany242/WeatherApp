package com.example.weather.model.localDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse

@Database(entities = [WeatherDBModel::class, LocationData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBase? = null
        fun getInstance(ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    WeatherDataBase::class.java,
                    "Weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}