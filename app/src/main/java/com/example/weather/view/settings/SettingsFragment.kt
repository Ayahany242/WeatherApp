package com.example.weather.view.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.localDataSource.LocalDataSourceImpl
import com.example.weather.model.localDataSource.sharedPreferences.SharedPreferencesDataSourceImp
import com.example.weather.model.repository.RepositoryImp
import com.example.weather.utils.Constants
import com.example.weather.viewModel.AppViewModel
import com.example.weather.viewModel.ViewModelFactory

class SettingsFragment : Fragment() {
    lateinit var binding:FragmentSettingsBinding
    private val viewModel: AppViewModel by lazy {
        val viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext()),
            sharedPreferencesDataSource = SharedPreferencesDataSourceImp(requireContext())
        ))
        ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.langArabic.setOnClickListener {
            viewModel.saveData(Constants.Language, Constants.LANGUAGE_AR)
        }

        binding.langEnglish.setOnClickListener {
            viewModel.saveData(Constants.Language, Constants.LANGUAGE_EN)
        }
        binding.celsius.setOnClickListener {
            viewModel.saveData(Constants.Temperature, Constants.UNITS_CELSIUS)
        }

        binding.kelvin.setOnClickListener {
            viewModel.saveData(Constants.Temperature, Constants.UNITS_kelvin)

        }
        binding.fahrenheit.setOnClickListener {
            viewModel.saveData(Constants.Temperature, Constants.UNITS_Fahrenheit)
        }
    }
}
/*

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var localDataSource = LocalDataSourceImp(requireContext())
        val remoteDataSource = RemoteDataSourceImp()
        val modelFactory by lazy { WeatherModelFactory(RepoImp.getInstance(remoteDataSource,localDataSource),requireContext()) }

        viewModel = ViewModelProvider(this,modelFactory).get(WeatherViewModel::class.java)

        val currentHour = LocalTime.now().hour

        if (currentHour >= 6 && currentHour < 18) {
            binding.root.setBackgroundResource(R.drawable.morning)
        } else {
            binding.root.setBackgroundResource(R.drawable.night)
        }

        binding.arabic.setOnClickListener {
           PreferenceManager.saveSelectedLanguage(requireContext(), Constants.LANGUAGE_AR)
        }

        binding.english.setOnClickListener {
            PreferenceManager.saveSelectedLanguage(requireContext(), Constants.LANGUAGE_EN)
        }
        binding.celsius.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_CELSIUS)
        }

        binding.kelvin.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_kelvin)

        }
        binding.fahrehite.setOnClickListener {
            PreferenceManager.saveSelectedTemperatureUnit(requireContext(), Constants.UNITS_Fahrenheit)
        }

        // Load the selected temperature unit from ViewModel and set the appropriate radio button checked
        val selectedUnit = PreferenceManager.getSelectedTemperatureUnit(requireContext())
        when (selectedUnit) {
            Constants.UNITS_kelvin -> binding.kelvin.isChecked = true
            Constants.UNITS_CELSIUS -> binding.celsius.isChecked = true
            Constants.UNITS_Fahrenheit -> binding.fahrehite.isChecked = true
        }
    }
}*/