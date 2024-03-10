package com.example.weather.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.home.viewModel.HomeViewModel
import com.example.weather.home.viewModel.HomeViewModelFactory
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.RepositoryImp

private const val TAG = "WeatherResponse"
class HomeFragment : Fragment() {
    lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModelFactory = HomeViewModelFactory(RepositoryImp.getInstance(RemoteDataSourceImp.getInstance()))
        homeViewModel= ViewModelProvider(requireActivity(),homeViewModelFactory).get(HomeViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getWeather()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    private fun getWeather(){
        homeViewModel.fetchWeather(lat=44.34,
            lon=10.99,
            apiKey = "1adef775b406ed6e948f449282cd6475",
            units="standard",
            lang="ar")
        Log.i(TAG, "getWeather: getWeather")
    }
}