package com.example.weather.model.localDataSource.sharedPreferences

import android.content.SharedPreferences

interface SharedPreferencesDataSource {
    fun saveData(key: String, value: String)
    fun getData(key: String, defaultValue: String): String
}