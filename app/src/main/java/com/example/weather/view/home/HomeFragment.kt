package com.example.weather.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.localDataSource.LocalDataSourceImpl
import com.example.weather.model.localDataSource.sharedPreferences.SharedPreferencesDataSourceImp
import com.example.weather.viewModel.AppViewModel
import com.example.weather.viewModel.ViewModelFactory
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.pojo.MinutelyItem
import com.example.weather.model.pojo.WeatherDBModel
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.repository.RepositoryImp
import com.example.weather.utils.API_KEY
import com.example.weather.utils.ApiStatus
import com.example.weather.utils.AppIcons
import com.example.weather.utils.Constants
import com.example.weather.utils.ConvertUnits
import com.example.weather.utils.LocationUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HomeActivity"
class HomeFragment : Fragment() {
    lateinit var viewModel :AppViewModel
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var binding: FragmentHomeBinding

    private val args: HomeFragmentArgs by navArgs()
    private var locationData: LocationData? =null
    private var isFavourite :Boolean = false

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var daysAdapter: DaysAdapter

    /*private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor*/

    private var currentLanguage: String = "en"
    private var tempUnit=Constants.Celsius
    private var temperatureUnit = Constants.UNITS_CELSIUS

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext()),
            sharedPreferencesDataSource = SharedPreferencesDataSourceImp(requireContext())
        ))
        viewModel= ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)
        checkLanguage(currentLanguage)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentLanguage = viewModel.getData(Constants.Language,Constants.LANGUAGE_EN)
        temperatureUnit = viewModel.getData(Constants.Temperature,Constants.UNITS_CELSIUS)

        Log.i(TAG, "onViewCreated: tempUnit=  $tempUnit , currentLanguage = $currentLanguage ,  ")
        locationData = args.locationData
        isFavourite = args.isFavourite
        locationData?.let {
            Log.i(TAG, "onCreate: onViewCreated Home Fragment latitude = ${it.latitude}, longitude = ${it.longitude}")
            if (!isFavourite){
                viewModel.saveData(Constants.CityName, locationData!!.cityName)
                viewModel.saveData(Constants.Latitude, locationData!!.latitude.toString())
                viewModel.saveData(Constants.Longitude, locationData!!.longitude.toString())
            }
            showWeather(it.latitude.toFloat(),it.longitude.toFloat())
        } ?: run {
            val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                val longitude = viewModel.getData(Constants.Longitude,"0").toFloat()
                val latitude = viewModel.getData(Constants.Latitude,"0").toFloat()
                showWeather(latitude,longitude)
            }else{
                getDataFromRoom()
                Log.i(TAG, "onViewCreated: localeData is null ")
            }


        }

    }

    private fun showWeather(latitude:Float, longitude:Float){
        viewModel.fetchWeather(lat=latitude,
            lon=longitude,
            apiKey = API_KEY,
            units=temperatureUnit,
            lang=currentLanguage)
        Log.i(TAG, "getWeather: getWeather")

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.weather.collectLatest { weatherStatus ->
                when (weatherStatus) {
                    is ApiStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ApiStatus.Success<*> -> {
                        val weatherData = weatherStatus.data as? WeatherResponse
                        if (weatherData != null) {
                            Log.i(TAG, "showWeather: weatherData != null")
                            binding.progressBar.visibility = View.GONE
                            setUpUI(weatherData)
                            val emptyList: List<MinutelyItem> = emptyList()
                            val weatherDBModel = WeatherDBModel(
                                1,
                                locationData?.cityName ?: "unKnown",
                                weatherData.current!!,
                                weatherData.daily,
                                weatherData.hourly,
                                weatherData.lat!!,
                                weatherData.lon!!,
                                emptyList,
                                weatherData.timezone!!,
                                weatherData?.timezoneOffset ?: 0
                            )
                            if (!isFavourite){
                                viewModel.deleteWeatherFromRoom(weatherDBModel)
                                viewModel.insertWeatherInRoom(weatherDBModel)
                                Log.i(TAG, "Saved current wither in database")
                            }
                        }else
                            Log.i(TAG, "showWeather: weatherData == null")
                    }

                    is ApiStatus.Error -> {
                        val errorMessage = weatherStatus.message // Access error message
                        Snackbar.make(requireView(),errorMessage,Snackbar.LENGTH_LONG).show()
                        getDataFromRoom()
                    }
                }
            }
        }
    }
    private fun getDataFromRoom() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAllWeatherFromRoom()
            viewModel.allWeatherFromRoom.collectLatest { db ->
                when (db) {
                    is ApiStatus.Success<*> -> {
                        val weatherResponseList = db.data as? List<WeatherDBModel>
                        if (!weatherResponseList.isNullOrEmpty()) {
                            val weather = weatherResponseList[0]
                            val weatherResponse = WeatherResponse(
                                null,
                                weather.current,
                                daily = weather.daily,
                                hourly = weather.hourly,
                                lat = weather.lat,
                                lon = weather.lon,
                                minutely = weather.minutely,
                                timezone = weather.timezone,
                                timezoneOffset = weather.timezone_offset
                            )
                            binding.progressBar.visibility = View.GONE
                           val cityName = LocationUtils.getCityName(requireContext(),
                                weatherResponse.lat!!.toDouble(), weatherResponse.lon!!.toDouble())
                            setUpUI(weatherResponse,cityName)
                            Log.i(TAG, "getDataFromRoom: weatherResponseList NOT Null ")
                        } else {
                            Log.i(TAG, "getDataFromRoom: weatherResponseList is Null ")
                        }
                    }
                    is ApiStatus.Error -> {
                        val errorMessage = db.message
                        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show()
                        Log.i(TAG, "getDataFromRoom: ApiStatus.Error")
                    }
                    is ApiStatus.Loading -> {
                        Snackbar.make(requireView(), "Loading the data ", Snackbar.LENGTH_LONG)
                            .show()
                        Log.i(TAG, "getDataFromRoom: ApiStatus.Loading")
                    }
                }
            }
        }
    }

    private fun setUpUI(response: WeatherResponse,cityName:String = "UnKnown"){
        hourlyAdapter = HourlyAdapter(tempUnit,response.timezone?:"")
        daysAdapter = DaysAdapter(tempUnit)

        displayCurrentWeatherData(response,cityName)

        binding.recyclerViewHourly.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                this.orientation = RecyclerView.HORIZONTAL
            }
            adapter = hourlyAdapter
        }
        binding.recyclerViewDays.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                this.orientation = RecyclerView.HORIZONTAL
            }
            adapter = daysAdapter
        }
        hourlyAdapter.submitList(response.hourly)
        daysAdapter.submitList(response.daily)
        Log.i(TAG, "onBindViewHolder: DaysAdapter ${response.daily?.size}")
        Log.i(TAG, "setUpUI: ${locationData?.cityName}" )
    }
    @SuppressLint("SetTextI18n")
    private fun displayCurrentWeatherData(response: WeatherResponse,cityName:String){
        binding.currentLocation.text = locationData?.cityName ?: cityName
        binding.currentDate.text = response.current?.dt?.let { getDateTime(it) }
        //response.current?.weather?.get(0)?.let { it.icon?.let { it1 -> AppIcons.getIcon(it1, binding.)}}
        binding.currentTemp.text =response.current?.temp.toString() + "\u00B0"
            //response.current?.temp?.let { ConvertUnits.convertTemp(it, tempUnit = tempUnit) }
        binding.currentWeatherState.text = response.current?.weather?.get(0)?.description
        Log.i(TAG, "binding.weatherState.text:${binding.currentWeatherState.text} ")
        response.current?.weather?.get(0)?.let {
            it.icon?.let { it1 ->
                AppIcons.getIcon(
                    it1, binding.currentWeatherStateIcon
                )
            }
        }
        Log.i(TAG, "displayCurrentWeatherData: description  ${response.current?.weather?.get(0)?.description}")

        binding.humidityDegree.text =response.current?.humidity.toString() + " %"
        binding.pressureDegree.text = response.current?.pressure.toString() + " hpa"

        val r = response.current?.windSpeed
        if (r != null)
            binding.windDegree.text = "$r m/s"
        else
            binding.windDegree.text = "0 m/s"
        binding.cloudyDegree.text = response.current?.clouds.toString() + " %"
    }
    private fun getDateTime(
        dt: Int,
        simpleDateFormat: SimpleDateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)
    ): String? {
        val cityTxtData = Date(dt.toLong() * 1000)
        return simpleDateFormat.format(cityTxtData)
    }

    private fun checkLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
    }
}