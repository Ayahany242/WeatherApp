package com.example.weather.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.model.pojo.LocationData
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class CurrentLocation(
    private val activity: Activity,
    private val context: Context,
    private val currentLocationResponse: CurrentLocationResponse
) {
    @SuppressLint("MissingPermission")
    fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                showEnableLocationDialog()
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions():Boolean{
        val result = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_ID_PERMISSION
        )
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5
        }

        LocationServices.getFusedLocationProviderClient(activity).lastLocation.addOnCompleteListener{ task->
            val location: Location? = task.result
            Log.i(LOCATIONTAG, "requestNewLocationData: result ${task.result}")
            location?.let {
                val city = LocationUtils.getCityName(context, location.latitude, it.longitude)
                val result = LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    cityName = city
                )
                currentLocationResponse.success(result)
                Log.i(LOCATIONTAG, "requestNewLocationData: success, city name is $city")
            } ?: run {
                Log.i(LOCATIONTAG, "requestNewLocationData: Failed to get location ")
                currentLocationResponse.failure("Failed to get location")
               // getLocation()
            }
        }.addOnFailureListener { e ->
            Log.e(LOCATIONTAG, "Failed to get location", e)
            //currentLocationResponse.failure(e.toString())
            getLocation()
        }
    }
}