package com.example.weather.model.pojo

data class Current(
    val sunrise: Int? ,
    val temp: Double? ,
    val visibility: Int? ,
    val uvi: Any? ,
    val pressure: Int? ,
    val clouds: Int? ,
    val feelsLike: Any? ,
    val windGust: Double?,
    val dt: Int? ,
    val windDeg: Int? ,
    val dewPoint: Double? ,
    val sunset: Int? ,
    val weather: List<WeatherItem?>?,
    val humidity: Int?,
    val windSpeed: Double?
)
