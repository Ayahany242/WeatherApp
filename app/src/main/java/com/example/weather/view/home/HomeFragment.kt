package com.example.weather.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
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

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var daysAdapter: DaysAdapter

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    var currentLanguage: String = "en"
    var tempUnit=Constants.Celsius

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext())))
        viewModel= ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)

        sharedPreference = requireActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        // Retrieve the stored values
        currentLanguage = sharedPreference.getString(Constants.Language, "en").toString()
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
        tempUnit= sharedPreference.getString(Constants.Temperature,Constants.Celsius).toString()
        var locationType = sharedPreference.getString(Constants.Location, "GPS")
        val cityName = sharedPreference.getString(Constants.CityName, "")
        val latitude = sharedPreference.getString(Constants.Latitude, "")
        val longitude = sharedPreference.getString(Constants.Longitude, "")
        locationData = LocationData(cityName = cityName!!,latitude = latitude!!.toDouble() , longitude = longitude!!.toDouble())
        latitude?.let { longitude?.let { it1 -> showWeather(it.toFloat(), it1.toFloat()) } }
       /* locationData = args.locationData
        locationData?.let {
            Log.i(TAG, "onCreate: onViewCreated Home Fragment latitude = ${it.latitude}, longitude = ${it.longitude}")
            editor.putString(Constants.Location, "GPS")
            editor.putString(Constants.CityName, locationData!!.cityName)
            editor.putString(Constants.Latitude, locationData!!.latitude.toString())
            editor.putString(Constants.Longitude, locationData!!.longitude.toString())
            editor.commit()

            //showWeather(it.latitude.toFloat(),it.longitude.toFloat())
        } ?: run {
            getDataFromRoom()
        }*/

    }

    private fun showWeather(latitude:Float, longitude:Float,lang:String = "en" ,units: String = "standard"){
        viewModel.fetchWeather(lat=latitude,
            lon=longitude,
            apiKey = API_KEY,
            units=units,
            lang=lang)
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
                            viewModel.deleteWeatherFromRoom(weatherDBModel)
                            viewModel.insertWeatherInRoom(weatherDBModel)
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
                            setUpUI(weatherResponse)
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

    private fun setUpUI(response: WeatherResponse){
        hourlyAdapter = HourlyAdapter(tempUnit,response.timezone?:"")
        daysAdapter = DaysAdapter(tempUnit)

        displayCurrentWeatherData(response)

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
    private fun displayCurrentWeatherData(response: WeatherResponse){
        binding.currentLocation.text = locationData?.cityName ?: "unKnown"
        binding.currentDate.text = response.current?.dt?.let { getDateTime(it) }
        //response.current?.weather?.get(0)?.let { it.icon?.let { it1 -> AppIcons.getIcon(it1, binding.)}}
        binding.currentTemp.text =
            response.current?.temp?.let { ConvertUnits.convertTemp(it, tempUnit = tempUnit) }
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