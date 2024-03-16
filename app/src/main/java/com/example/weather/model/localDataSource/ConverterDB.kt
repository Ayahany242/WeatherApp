package com.example.weather.model.localDataSource

import androidx.room.TypeConverter
import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.DailyItem
import com.example.weather.model.pojo.HourlyItem
import com.example.weather.model.pojo.MinutelyItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCurrent(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun toCurrent(value: String): Current {
        val type = object : TypeToken<Current>() {}.type
        return Gson().fromJson(value, type)
    }
    @TypeConverter
    fun fromDaily(daily: List<DailyItem>): String {
        return Gson().toJson(daily)
    }

    @TypeConverter
    fun toDaily(value: String): List<DailyItem> {
        val type = object : TypeToken<List<DailyItem>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromHourly(hourly: List<HourlyItem>): String {
        return Gson().toJson(hourly)
    }

    @TypeConverter
    fun toHourly(value: String): List<HourlyItem> {
        val type = object : TypeToken<List<HourlyItem>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromMinutely(minutely: List<MinutelyItem>): String {
        return Gson().toJson(minutely)
    }

    @TypeConverter
    fun toMinutely(value: String): List<MinutelyItem> {
        val type = object : TypeToken<List<MinutelyItem>>() {}.type
        return Gson().fromJson(value, type)
    }
}