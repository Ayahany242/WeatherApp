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
import kotlinx.coroutines.launch
import java.util.Locale

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: AppViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var currentLocation: LocationData
    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var locationType: String = ""
    lateinit var binding: FragmentMapsBinding

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val defaultLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        map.addMarker(MarkerOptions().position(defaultLocation).title(currentLocation.cityName))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,10f))
        map.setOnMapLongClickListener { latlong ->

            if (locationType.equals("Map")) {
                displayAlertDialogToSaveCurrentLocation(currentLocation)
            } else {
                displayAlertDialogToSaveFavourite(currentLocation)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext())))
        viewModel= ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =
            requireActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        currentLocation = getDefaultLocationWhenOpenMapScreen()
        locationType = MapsFragmentArgs.fromBundle(requireArguments()).location.toString()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        //locationDatabase = FavLocationDB.getInstance(requireContext())
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
                REQUEST_LOCATION_PERMISSION
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
            showSaveLocationDialog(latLng.latitude, latLng.longitude)
        }
    }

    private fun showSaveLocationDialog(latitude: Double, longitude: Double) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Save Location")

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_favourite_place, null)

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        val country: String? = addresses?.get(0)?.countryName
        val city: String? = addresses?.get(0)?.locality

        builder.setView(dialogView)

        builder.setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
            country?.let { c ->
                city?.let { ci ->
                    val locationData = LocationData(
                        cityName = ci,
                        latitude = latitude,
                        longitude = longitude
                    )
                    insertLocationIntoDB(locationData)
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun displayAlertDialogToSaveFavourite(locationData: LocationData) {
        if (locationData != null) {
            var alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alert.setTitle(getString(R.string.Save_In_Favourite))
            alert.setMessage(getString(R.string.Dialog_Save_Fav_Message))
            alert.setPositiveButton(getString(R.string.Save)) { _: DialogInterface, _: Int ->

                viewModel.insertFavourite(locationData)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.Save_Successfull),
                    Toast.LENGTH_SHORT
                ).show()
                NavHostFragment.findNavController(this@MapsFragment).navigate(R.id.action_mapsFragment_to_favouritePlaceFragment)
            }
            alert.setNegativeButton(getString(R.string.Cancel)) { _: DialogInterface, _: Int ->
            }

            val dialog = alert.create()

            dialog.show()
        }

    }
    fun displayAlertDialogToSaveCurrentLocation(locationData: LocationData) {
        if (locationData != null) {
            var alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alert.setTitle(getString(R.string.Save_Location))
            alert.setMessage(getString(R.string.Dialog_Save_Map_Message))
            alert.setPositiveButton(getString(R.string.Save)) { _: DialogInterface, _: Int ->
                editor.putString(Constants.Location, "Map")
                editor.putString(Constants.CityName, locationData.cityName)
                editor.putString(Constants.Latitude, (locationData.latitude).toString())
                editor.putString(Constants.Longitude, (locationData.longitude).toString())
                editor.commit()

                NavHostFragment.findNavController(this@MapsFragment).navigate(R.id.action_mapsFragment_to_homeFragment)

            }
            alert.setNegativeButton(getString(R.string.Cancel)) { _: DialogInterface, _: Int ->
            }

            val dialog = alert.create()
            dialog.show()
        }

    }

    private fun insertLocationIntoDB(locationData: LocationData) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.insertFavourite(locationData)
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val DEFAULT_ZOOM = 15f
    }

    fun getDefaultLocationWhenOpenMapScreen(): LocationData {
        val city = sharedPreference.getString(Constants.CityName, "")
        val lat = sharedPreference.getString(Constants.Latitude, "0.0")
        val lng = sharedPreference.getString(Constants.Longitude, "0.0")
        currentLocation = LocationData(city!!, lat?.toDouble() ?: 0.0, lng?.toDouble() ?: 0.0)
        return currentLocation
    }
}