package com.example.weather.view.favourite

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.weather.R
import com.example.weather.databinding.FragmentMapsBinding
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.localDataSource.LocalDataSourceImpl
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.repository.RepositoryImp
import com.example.weather.utils.Constants
import com.example.weather.utils.LOCATION_ID_PERMISSION
import com.example.weather.utils.LocationUtils
import com.example.weather.viewModel.AppViewModel
import com.example.weather.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

private const val TAG = "MapsFragment"
class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: AppViewModel by lazy {
        val viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext())))
        ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)
    }
    //private lateinit var locationDatabase: FavLocationDB

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        getCurrentLocation()
        setMapClickListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_ID_PERMISSION
            )
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                    map.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
                }
            }
    }

    private fun setMapClickListener() {
        map.setOnMapClickListener { latLng ->
            map.clear() // Clear existing markers
            map.addMarker(MarkerOptions().position(latLng).title("Chosen Location"))
            val city: String = LocationUtils.getCityName(requireContext(), latitude = latLng.latitude, longitude = latLng.longitude)
            displayAlertDialogToSaveFavourite(LocationData(city,latLng.latitude,latLng.longitude))
        }
    }
    private fun displayAlertDialogToSaveFavourite(locationData: LocationData) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alert.setTitle(getString(R.string.Save_In_Favourite))
        alert.setMessage(getString(R.string.Dialog_Save_Fav_Message))
        alert.setPositiveButton(getString(R.string.Save)) { _: DialogInterface, _: Int ->
            viewModel.insertFavourite(locationData)
            Toast.makeText(
                requireContext(),
                getString(R.string.Save_Successfull),
                Toast.LENGTH_SHORT
            ).show()
            Log.i(TAG, "displayAlertDialogToSaveFavourite: ${locationData.cityName}")
            NavHostFragment.findNavController(this@MapsFragment)
                .navigate(R.id.action_mapsFragment_to_favouritePlaceFragment)
        }
        alert.setNegativeButton(getString(R.string.Cancel)) { _: DialogInterface, _: Int ->
        }
        val dialog = alert.create()
        dialog.show()
    }

    companion object {
        private const val DEFAULT_ZOOM = 10f
    }
}