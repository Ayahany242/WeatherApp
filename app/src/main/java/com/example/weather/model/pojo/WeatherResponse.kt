package com.example.weather.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class WeatherResponse(
    val alerts: List<AlertsItem?>? = null,
    val current: Current? = null,
    val timezone: String? = null,
    val timezoneOffset: Int? = null,
    val daily: List<DailyItem?>? = null,
    val lon: Double? = null,
    val hourly: List<HourlyItem?>? = null,
    val minutely: List<MinutelyItem?>? = null,
    val lat: Double? = null
)

@Entity(tableName = "Weather_Table")
data class WeatherDBModel(
    @PrimaryKey
    val id:Int,
    val city:String,
    val current: Current,
    val daily: List<DailyItem?>?,
    val hourly: List<HourlyItem?>?,
    val lat: Double,
    val lon: Double,
    val minutely: List<MinutelyItem>,
    val timezone: String,
    val timezone_offset: Int
):Serializable
