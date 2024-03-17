package com.example.weather.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.model.pojo.Current
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.repository.Repository
import com.example.weather.utils.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AppViewModelTest{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: AppViewModel
    lateinit var repository: Repository

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

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


    @Before
    fun setUp(){
        repository = FakeRepository()
        viewModel = AppViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun end(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
    @Test
    fun fetchWeather_sendWeatherData_LoadingResponse()= testDispatcher.runBlockingTest{
        val lat = 0.0f
        val lon = 0.0f
        val appid = "your_app_id"
        val units = "stander"
        val lang = "en"

        //When: Call the function
        viewModel.fetchWeather(lat, lon, appid, units, lang)
        //Then: check the result
        val result = viewModel.weather.value
        assertThat(result, `is`(instanceOf(ApiStatus.Loading::class.java)))
    }

    @Test
    fun insertWeatherInRoom_Loading() = testDispatcher.runBlockingTest {
        // When: Call the function
        viewModel.insertWeatherInRoom(weather)

        // Then: Check the result
        val result = viewModel.weather.value
        assertThat(
            result,
            `is`(instanceOf(ApiStatus.Loading::class.java))
        )
    }
    @Test
    fun getAllFavourite_getAllLists1() = testDispatcher.runBlockingTest {
        // When: Call the function
        viewModel.getAllFavourite()

        // Then: Check the result
        val result = viewModel.allFavourites.value
        assertThat(
            result?.size, `is`(1)
        )
    }
    @Test
    fun deleteWeatherFromRoom_Loading() = runBlockingTest{
        viewModel.deleteWeatherFromRoom(weather)

        // Then: Check the result
        val result = viewModel.weather.value
        assertThat(
            result,
            `is`(instanceOf(ApiStatus.Loading::class.java))
        )
    }
    @Test
    fun getAllWeatherFromRoom()= testDispatcher.runBlockingTest{
        viewModel.getAllWeatherFromRoom()

        // Then: Check the result
        val result = viewModel.weather.value
        assertThat(
            result,
            `is`(instanceOf(ApiStatus.Loading::class.java))
        )
    }
    @Test
    fun insertFavourite_getAllLists2() = testDispatcher.runBlockingTest {
        val locationData = LocationData(cityName = "ismailia", latitude = 1.0, longitude = 1.0)
        viewModel.insertFavourite(locationData)
        // When: Call the function
       // viewModel.getAllFavourite()

        // Then: Check the result
        val result = viewModel.allFavourites.value
        assertThat(
            result?.size, `is`(1)
        )
    }
    @Test
    fun deleteFavourite_getAllLists1() = testDispatcher.runBlockingTest {
        val locationData = LocationData(cityName = "is", latitude = 1.0, longitude = 1.0)
        viewModel.deleteFavourite(locationData)
        // When: Call the function
        viewModel.getAllFavourite()

        // Then: Check the result
        val result = viewModel.allFavourites.value
        assertThat(
            result?.size, `is`(1)
        )
    }

}