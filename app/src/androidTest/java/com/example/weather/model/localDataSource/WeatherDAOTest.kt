package com.example.weather.model.localDataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDAOTest {
    @get:Rule
    var instantExceptionRule = InstantTaskExecutorRule()
    val current = Current(
        null, null, null, null, null, null, null, null, null, null, null, null,
        emptyList(), null, null
    )
    val weather = WeatherDBModel(
        id = 1, city = "New York",
        timezone = "t",
        timezone_offset = 1,
        minutely = emptyList(),
        lon = 1.0, lat = 1.0,
        current = current, daily = emptyList(),
        hourly = emptyList()
    )
    val weather2 = WeatherDBModel(
        id = 2, city = "New York",
        timezone = "t",
        timezone_offset = 1,
        minutely = emptyList(),
        lon = 1.0, lat = 1.0,
        current = current, daily = emptyList(),
        hourly = emptyList()
    )

    val locationData = LocationData(cityName = "Paris", latitude = 1.0, longitude = 1.0)
    val locationData1 = LocationData(cityName = "Ismailia", latitude = 1.0, longitude = 1.0)

    lateinit var dataBase: WeatherDataBase


    @Before
    fun SetUp() {
        dataBase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
    }

    @After
    fun end() {
        dataBase.close()
    }

    @Test
    fun insertWeather() = runBlocking {
        val result = dataBase.getWeatherDao().insertWeather(weather)
        val response = dataBase.getWeatherDao().getAllWeather().first()
        assertThat(result, `is`(1L))
        assertThat(response[0].id, `is`(weather.id))
    }

    @Test
    fun deleteWeather() = runBlocking {
        val result = dataBase.getWeatherDao().insertWeather(weather)
        val resultDelete = dataBase.getWeatherDao().deleteWeather(weather)
        val response = dataBase.getWeatherDao().getAllWeather().first()

        assertThat(result, `is`(1L))
        assertThat(resultDelete, `is`(1))
        assertThat(response.size, `is`(0))
    }

    @Test
    fun getAllWeather() = runBlocking{
        dataBase.getWeatherDao().insertWeather(weather)
        dataBase.getWeatherDao().insertWeather(weather2)

        val response = dataBase.getWeatherDao().getAllWeather().firstOrNull()
        assertThat(response, hasSize(2))
        assertThat(response?.get(0)?.id,`is`(weather.id))
    }

    @Test
    fun getAllFavourites() = runBlocking {
        dataBase.getWeatherDao().insertFavourite(locationData)
        dataBase.getWeatherDao().insertFavourite(locationData1)

        val response = dataBase.getWeatherDao().getAllFavourites().firstOrNull()
        assertThat(response, hasSize(2))
        assertThat(response?.get(0)?.cityName,`is`(locationData.cityName))
    }
    @Test
    fun insertFavourite() = runBlocking {
       dataBase.getWeatherDao().insertFavourite(locationData)
        val response = dataBase.getWeatherDao().getAllFavourites().first()

        assertThat(response[0].cityName, `is`(locationData.cityName))
    }
    @Test
    fun deleteFavourite() = runBlocking {
        dataBase.getWeatherDao().deleteFavourite(locationData)
        val response = dataBase.getWeatherDao().getAllFavourites().first()
        assertThat(response.size, `is`(0))
    }

}