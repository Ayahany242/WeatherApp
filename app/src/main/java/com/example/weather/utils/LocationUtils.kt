package com.example.weather.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat

object LocationUtils {
    private const val TAG = "LocationUtils"

    fun getCityName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context)
        return try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val cityName = addresses[0].locality
                cityName ?: "Unknown City"
            } else {
                "Unknown City"
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error getting city name: ${e.message}", e)
            "Unknown City"
        }
    }
}
class ConvertUnits {
    companion object{
        private  var approximateTemp=ApproximateTemp()

        fun convertTemp(temp:Double,tempUnit:String) :String{
            var result=""
            when(tempUnit){
                Constants.Celsius -> result=approximateTemp(temp - 273.15)+ "\u00B0"
                Constants.Kelvin -> result=approximateTemp(273.5 + ((temp - 32.0) * (5.0/9.0)))+ "\u00B0"
                Constants.Fahrenheit -> result=approximateTemp(temp)+ "\u00B0"
                else -> result=approximateTemp(temp)+ "\u00B0"
            }
            return result
        }
    }
}

class ApproximateTemp{
    operator fun invoke(temp:Double): String=approximate(temp)
    private fun approximate(num:Double):String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(num)
    }
}