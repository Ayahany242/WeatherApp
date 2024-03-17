package com.example.weather.view.favourite

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentFavouritePlaceBinding
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.localDataSource.LocalDataSourceImpl
import com.example.weather.model.pojo.LocationData
import com.example.weather.model.repository.RepositoryImp
import com.example.weather.viewModel.AppViewModel
import com.example.weather.viewModel.ViewModelFactory
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "FavouritePlaceFragment"
class FavouritePlaceFragment : Fragment() {
    lateinit var binding: FragmentFavouritePlaceBinding
    private val viewModel: AppViewModel by lazy {
        val viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
        remote = RemoteDataSourceImp.getInstance(),
        local = LocalDataSourceImpl.getInstance(requireContext())))
       ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)
    }

    lateinit var favouriteAdapter: FavouriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritePlaceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllFavourite()
        binding.fab.setOnClickListener(){
            NavHostFragment.findNavController(this@FavouritePlaceFragment)
                .navigate(R.id.action_favouritePlaceFragment_to_mapsFragment)
        }
        favouriteAdapter = FavouriteAdapter(deleteClickListener = showDeleteAlertDialog, cardClickListener = showTheselectedCityData)
        lifecycleScope.launch {
            viewModel.allFavourites.collect { favourites ->
                Log.i(TAG, "\":allFavourites:  ${favourites?.size}")
                favouriteAdapter.submitList(favourites)
            }
        }
        binding.favRecyclerView.apply {
            adapter = favouriteAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                this.orientation = RecyclerView.VERTICAL
            }
        }
    }
    var showDeleteAlertDialog :(LocationData) -> Unit = {
        val alert: AlertDialog.Builder= AlertDialog.Builder(requireContext())
        alert.setTitle(getString(R.string.Delete_Fav_Location))
        alert.setMessage(getString(R.string.Dialog_Delete_Fav_Message))
        alert.setPositiveButton(getString(R.string.Delete)){ _: DialogInterface, _: Int ->
            viewModel.deleteFavourite(it)
            Log.i(TAG, "\":showDeleteAlertDialog Deleted Succesfully ${it.cityName} ")
            NavHostFragment.findNavController(this@FavouritePlaceFragment).navigate(R.id.action_favouritePlaceFragment_self)
        }
        alert.setNegativeButton(getString(R.string.Cancel)){ _: DialogInterface, _: Int ->
        }
        val dialog=alert.create()
        dialog.show()
    }


    var showTheselectedCityData: (LocationData) -> Unit = { locationData ->
        view?.post {
            val action = FavouritePlaceFragmentDirections.actionFavouritePlaceFragmentToHomeFragment(locationData = locationData, isFavourite = true)
            Log.i(TAG, "Location data sent from fav to home: $locationData")
            findNavController().navigate(action)
        }
    }
}