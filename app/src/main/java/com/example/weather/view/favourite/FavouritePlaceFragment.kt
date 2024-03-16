package com.example.weather.view.favourite

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
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

class FavouritePlaceFragment : Fragment() {
    lateinit var binding: FragmentFavouritePlaceBinding
    lateinit var viewModel: AppViewModel
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var favouriteAdapter: FavouriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritePlaceBinding.inflate(layoutInflater, container, false)
        viewModelFactory = ViewModelFactory( RepositoryImp.getInstance(
            remote = RemoteDataSourceImp.getInstance(),
            local = LocalDataSourceImpl.getInstance(requireContext())))
        viewModel= ViewModelProvider(requireActivity(),viewModelFactory).get(AppViewModel::class.java)
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
            viewModel.allFavourites.collect {
                favouriteAdapter.submitList(it)
                binding.favRecyclerView.apply {
                    adapter = favouriteAdapter
                    layoutManager = LinearLayoutManager(requireContext()).apply {
                        this.orientation = RecyclerView.VERTICAL
                    }
                }
            }
        }
    }
    var showDeleteAlertDialog :(LocationData) -> Unit = {
        var alert: AlertDialog.Builder= AlertDialog.Builder(requireContext())
        alert.setTitle(getString(R.string.Delete_Fav_Location))
        alert.setMessage(getString(R.string.Dialog_Delete_Fav_Message))
        alert.setPositiveButton(getString(R.string.Delete)){ _: DialogInterface, _: Int ->
            viewModel.deleteFavourite(it)
            //NavHostFragment.findNavController(this@FavFragment).navigate(R.id.action_favFragment_self)
        }
        alert.setNegativeButton(getString(R.string.Cancel)){ _: DialogInterface, _: Int ->
        }
        var dialog=alert.create()
        dialog.show()
    }
    var showTheselectedCityData: (LocationData)->Unit = {

    }
}