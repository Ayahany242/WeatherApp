package com.example.weather.model.localDataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
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
@MediumTest
class LocalDataSourceImplTest{
    @get:Rule
    var instantExceptionRule = InstantTaskExecutorRule()

    lateinit var dataBase: WeatherDataBase
    lateinit var localDataSource: LocalDataSource

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
    @Before
    fun setUp(){
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = LocalDataSourceImpl.getInstance(ApplicationProvider.getApplicationContext())
    }

    @After
    fun end() {
        dataBase.close()
    }
    @Test
    fun insertCurrentWeather() = runBlocking {
        val result = localDataSource.insertCurrentWeather(weather)
        val response = localDataSource.getAllWeather().first()
        assertThat(result, CoreMatchers.`is`(1L))
        assertThat(response[0].id, CoreMatchers.`is`(weather.id))
    }
   /* @Test
    fun deleteCurrentWeather_noWeatherExisted() = runBlocking {
        val result = localDataSource.deleteCurrentWeather(weather)
        val response = localDataSource.getAllWeather().firstOrNull()
        assertThat(result, `is`(0))
        assertThat(response, `is`(emptyList()))
    }*/
    @Test
    fun getAllWeather() = runBlocking {
        localDataSource.insertCurrentWeather(weather)
        localDataSource.insertCurrentWeather(weather2)

        val response = localDataSource.getAllWeather().firstOrNull()
        assertThat(response, hasSize(2))
        assertThat(response?.get(0)?.id, `is`(weather.id))
    }
    @Test
    fun getAllFavourites() = runBlocking {
        localDataSource.insertFavourite(locationData)
        localDataSource.insertFavourite(locationData1)

        val response = localDataSource.getAllFavourites().firstOrNull()
        assertThat(response, hasSize(2))
        assertThat(response?.get(0)?.cityName, `is`(locationData.cityName))
    }

    /*@Test
    fun getAllFavourites_noFavouriteExist() = runBlocking {

        val response = localDataSource.getAllFavourites().firstOrNull()
        assertThat(response,`is`(emptyList()))
    }*/
    @Test
    fun insertFavourite() = runBlocking {
        localDataSource.insertFavourite(locationData)
        val response = localDataSource.getAllFavourites().first()
        assertThat(response[0].cityName,`is`(locationData.cityName))
    }
    @Test
    fun deleteFavourite() = runBlocking {
        localDataSource.deleteFavourite(locationData)
        val response = localDataSource.getAllFavourites().firstOrNull()
        assertThat(response,`is`(emptyList()))
    }

}