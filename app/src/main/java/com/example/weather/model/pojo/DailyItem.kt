package com.example.weather.model.pojo

data class DailyItem(
    val moonset: Int? ,
    val summary: String? ,
    val rain: Any? ,
    val sunrise: Int?,
    val temp: Temp? ,
    val moonPhase: Double? ,
    val uvi: Double?,
    val moonrise: Int? ,
    val pressure: Int?,
    val clouds: Int? ,
    val feelsLike: FeelsLike? ,
    val windGust: Double?,
    val dt: Int?, //day
    val pop: Double?,
    val windDeg: Int? ,
    val dewPoint: Double?,
    val sunset: Int? ,
    val weather: List<WeatherItem?>? ,
    val humidity: Int?,
    val windSpeed: Double?,
    val snow: Any?
)
