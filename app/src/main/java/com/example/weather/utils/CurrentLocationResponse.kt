package com.example.weather.utils

import android.location.Address
import com.example.weather.model.pojo.LocationData

interface CurrentLocationResponse {
    fun success(response:LocationData)
    fun failure(msg:String)
}