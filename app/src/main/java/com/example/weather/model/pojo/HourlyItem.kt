package com.example.weather.model.pojo

data class HourlyItem(
    val temp: Any? = null,
    val visibility: Int? = null,
    val uvi: Any? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: Any? = null,
    val windGust: Any? = null,
    val dt: Int? = null,
    val pop: Any? = null,
    val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Any? = null,
    val rain: Rain? = null
)
