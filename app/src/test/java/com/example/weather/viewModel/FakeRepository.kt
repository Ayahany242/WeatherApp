package com.example.weather.viewModel

import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRepository :Repository{
    val current = Current(null,null,null,null,null,null,null,null,null,null,null,null,
        emptyList(),null,null
    )
    private val fakeWeatherData: MutableList<WeatherDBModel> = mutableListOf(WeatherDBModel(id = 1, city = "New York",
        timezone = "t",
        timezone_offset = 1,
        minutely = emptyList(),
        lon = 1.0, lat = 1.0,
        current =current, daily = emptyList(),
        hourly = emptyList() ))
    private val fakeFavouritesData: MutableList<LocationData> = mutableListOf(LocationData(cityName = "Paris", latitude = 1.0, longitude = 1.0))
    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        appid: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        val weatherResponse = WeatherResponse(
            alerts = null,
            current = null,
            timezone = null,
            timezoneOffset = null,
            daily = null,
            hourly = null,
            minutely = null,
            lat = lat.toDouble(),
            lon = lon.toDouble()
        )
        return Response.success(weatherResponse)
    }
        override suspend fun getAllWeather(): Flow<List<WeatherDBModel>> {
            return flow { emit(fakeWeatherData) }
        }

        override suspend fun insertWeather(weather: WeatherDBModel): Long {
            fakeWeatherData.add(weather)
            return 1L
        }

        override suspend fun deleteWeather(weather: WeatherDBModel): Int {
            fakeWeatherData.remove(weather)
            return 1
        }

        override suspend fun getAllFavourites(): Flow<List<LocationData>> {
            return flow { emit(fakeFavouritesData) }
        }

        override suspend fun insertFavourite(favouriteCity: LocationData) {
            fakeFavouritesData.add(favouriteCity)
        }

        override suspend fun deleteFavourite(favouriteCity: LocationData) {
            fakeFavouritesData.remove(favouriteCity)
        }

        override fun getData(key: String, defaultValue: String): String {
            return ""
        }

        override fun saveData(key: String, value: String) {
        }


}