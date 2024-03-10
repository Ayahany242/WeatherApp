package com.example.weather.utils

sealed class ApiStatus{
    object Loading : ApiStatus()
    data class Success<T>(val data: T) : ApiStatus()
    data class Error(val exception: Exception) : ApiStatus()
}