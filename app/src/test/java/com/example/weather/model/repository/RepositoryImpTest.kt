package com.example.weather.model.repository

import com.example.weather.model.RemoteDataSource.RemoteDataSource
import com.example.weather.model.localDataSource.LocalDataSource
import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class RepositoryImpTest{
    //Given: Prepare data
    private lateinit var repository: Repository
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    val current = Current(null,null,null,null,null,null,null,null,null,null,null,null,
    emptyList(),null,null
    )
    val weather = WeatherDBModel(id = 1, city = "New York",
        timezone = "t",
        timezone_offset = 1,
        minutely = emptyList(),
        lon = 1.0, lat = 1.0,
        current =current, daily = emptyList(),
        hourly = emptyList() )
    val locationData = LocationData(cityName = "Paris", latitude = 1.0, longitude = 1.0)
    @Before
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()
        repository = RepositoryImp(remoteDataSource, localDataSource)
    }
    @Test
    fun getCurrentWeather_weatherData_responseSuccessful()=runBlocking {
        val lat = 0.0f
        val lon = 0.0f
        val appid = "your_app_id"
        val units = "stander"
        val lang = "en"

        val weatherData = WeatherResponse(lat = lat.toDouble(), lon = lon.toDouble())
        //When: Call the function
        val response = repository.getCurrentWeather(lat, lon, appid, units, lang)
        //Then: check the result
        assertThat(response.isSuccessful, `is`(true))
        assertThat(response.body(), `is`(notNullValue()))
        assertThat(response.body(), `is`(equalTo(weatherData)))
    }

    //testing local
    @Test
    fun insertWeather_weatherInput_resultIs1Long() = runBlocking {
        val result = repository.insertWeather(weather)
        //assertEquals(1L, id)
        assertThat(result,`is`(1L))
    }
    @Test
    fun deleteCurrentWeather_insertWeather_1Success() = runBlocking {
        repository.insertWeather(weather)
        val result = repository.deleteWeather(weather)
        assertThat(result,`is`(1))
    }
    @Test
    fun deleteCurrentWeather_noWeatherStored_0Success() = runBlocking {
        val result = repository.deleteWeather(weather)
        assertThat(result,`is`(0))
    }
    @Test
    fun getAllWeather_insertWeather_insertedWeather() = runBlocking {
        repository.insertWeather(weather)
        val result = repository.getAllWeather().firstOrNull()
        assertThat(result?.get(0), `is`(equalTo(weather)))
    }
    @Test
    fun insertFavourite_locationData_updatedList() = runBlocking {
        repository.insertFavourite(locationData)
        val resultList = repository.getAllFavourites().firstOrNull()
        assertThat(resultList, hasSize(1))
        assertThat(resultList?.get(0), `is`(equalTo(locationData)))
    }
    @Test
    fun deleteFavourite_insertedLocation_emptyList() = runBlocking {
        repository.insertFavourite(locationData)
        repository.deleteFavourite(locationData)
        val result = repository.getAllFavourites().firstOrNull()
        assertThat(result, empty())
    }
    @Test
    fun deleteFavourite_emptyList_emptyList() = runBlocking {
        repository.deleteFavourite(locationData)
        val result = repository.getAllFavourites().firstOrNull()
        assertThat(result, empty())
    }
    @Test
    fun getAllFavourites_listOf2_ListSize2() = runBlocking{
        repository.insertFavourite(locationData)
        repository.insertFavourite(locationData)
        val result = repository.getAllFavourites().firstOrNull()
        assertThat(result, hasSize(2))
        assertThat(result?.get(0), `is`(equalTo(locationData)))
    }
}