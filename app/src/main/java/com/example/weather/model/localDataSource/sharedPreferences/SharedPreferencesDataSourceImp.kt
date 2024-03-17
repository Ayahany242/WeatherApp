package com.example.weather.model.localDataSource.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesDataSourceImp(context:Context):SharedPreferencesDataSource {
    private val sharedPreferences = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)

    override fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}