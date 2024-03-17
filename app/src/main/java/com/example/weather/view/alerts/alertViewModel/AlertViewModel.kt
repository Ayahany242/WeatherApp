package com.example.weather.view.alerts.alertViewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.Repository
import com.example.weather.utils.ApiStatus
import com.example.weather.view.alerts.AlertPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val weatherRepo: Repository):ViewModel() {

    var alertList : MutableStateFlow<ApiStatus> = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    var alerts :StateFlow<ApiStatus> = alertList

    init {
        getAlerts()
    }

    fun getAlerts()
    {
        viewModelScope.launch {
            /*weatherRepo.getAllAlertLocations()
                .catch {
                   // alertList.value = ApiStatus.Error(it.message.toString())

                }.collect{
                  // alertList.value = ApiStatus.Success(it)
                }*/
        }
    }

    fun addAlert(alertPojo: AlertPojo)
    {
        viewModelScope.launch(Dispatchers.IO) {
           // weatherRepo.insertAlertLocation(alertPojo)
            getAlerts()
        }
    }

    fun delAlert(alertPojo: AlertPojo)
    {
        viewModelScope.launch(Dispatchers.IO) {
            //weatherRepo.delAlertLocation(alertPojo)
            getAlerts()
        }
    }
}