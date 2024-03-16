package com.example.weather


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.model.pojo.LocationData
import com.example.weather.utils.Constants
import com.example.weather.utils.CurrentLocation
import com.example.weather.utils.CurrentLocationResponse
import com.example.weather.utils.LOCATIONTAG
import com.example.weather.utils.LOCATION_ID_PERMISSION
import com.example.weather.utils.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

private const val TAG = "HomeActivity"
class MainActivity : AppCompatActivity(), CurrentLocationResponse{
    //lateinit var currentLocation: CurrentLocation
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onResume() {
        super.onResume()
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            /*currentLocation =
                CurrentLocation(this@MainActivity, this, this)
            currentLocation.getLocation()*/
            getLocation()
            Log.i(TAG, "onResume: get location ")
        }else{
            Toast.makeText(this,"Please Connect to the internet",Toast.LENGTH_LONG).show()
            Log.i(TAG, "onResume: failed to get location bc of network ")
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun success(response: LocationData) {
        Log.i(LOCATIONTAG, "success: Main Activity ")
        val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
            putExtra("locationData",response)
        }
        startActivity(intent)
        finish()
    }

    override fun failure(msg: String) {
        Log.i(TAG, "failure: failed to get location ")
       Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_ID_PERMISSION) {
            Log.i(LOCATIONTAG, "onRequestPermissionsResult: ")

            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //currentLocation.getLocation()
                Log.i(LOCATIONTAG, "onRequestPermissionsResult:grantResults.isNotEmpty() ")

            }else{
                Log.i(LOCATIONTAG, "onRequestPermissionsResult:  ")
            }
        }
    }
    private fun checkPermissions():Boolean{
        val result = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_ID_PERMISSION
        )
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
        this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    /*@SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5
        }

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnCompleteListener{ task->
            val location: Location? = task.result
            Log.i(LOCATIONTAG, "requestNewLocationData: result ${task.result}")
            location?.let {
                val city = LocationUtils.getCityName(this, location.latitude, it.longitude)
                val result = LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    cityName = city
                )
                success(result)
                Log.i(LOCATIONTAG, "requestNewLocationData: success, city name is $city")
            } ?: run {
                Log.i(LOCATIONTAG, "requestNewLocationData: Failed to get location ")
                failure("Failed to get location")
                // getLocation()
            }
        }.addOnFailureListener { e ->
            Log.e(LOCATIONTAG, "Failed to get location", e)
            //currentLocationResponse.failure(e.toString())
            getLocation()
        }
    }*/
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5
        }
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallBack,
            Looper.myLooper()
        )
    }
    private val mLocationCallBack: LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location: Location? = p0.lastLocation
            Log.i(LOCATIONTAG, "success: Main Activity ")
            location?.let {
                val city = LocationUtils.getCityName(this@MainActivity, location.latitude, it.longitude)
                val result = LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    cityName = city
                )
                sharedPreference =
                    this@MainActivity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)

                editor = sharedPreference.edit()
                editor.putString(Constants.Location, "GPS")
                editor.putString(Constants.CityName, result.cityName)
                editor.putString(Constants.Latitude, result.latitude.toString())
                editor.putString(Constants.Longitude, result.longitude.toString())
                editor.commit()

                val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                    putExtra("locationData",result)
                }
                startActivity(intent)
                finish()
                fusedLocationClient.removeLocationUpdates(this)
                Log.i(LOCATIONTAG, "requestNewLocationData: success, city name is $city")
            } ?: run {
                Log.i(LOCATIONTAG, "requestNewLocationData: Failed to get location ")
                failure("Failed to get location")
                // getLocation()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(mLocationCallBack)
    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(mLocationCallBack)
    }
}