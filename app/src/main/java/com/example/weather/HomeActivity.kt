package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weather.databinding.ActivityHomeBinding

private const val TAG = "HomeActivity"
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var navController : NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val actionBar : ActionBar? = supportActionBar
        actionBar.run {
            this!!.setHomeAsUpIndicator(R.drawable.menu_icon)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
           // supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@HomeActivity, R.color.primaryColor)))
        }
        navController = Navigation.findNavController(this,R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(binding.navigatorView,navController)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /*intent.getParcelableExtra<LocationData>("locationData")?.let { locationData ->

            *//*Log.i(LOCATIONTAG, "onCreate: HomeActivity city Name is ${locationData.cityName}")
            val action = HomeFragmentDirections.actionHomeFragmentSelf(locationData)
            val navController = supportFragmentManager.findFragmentById(R.id.nav_host)?.findNavController()
            navController?.navigate(action)*//*
        }?: run {
            Log.i(LOCATIONTAG, "onCreate: HomeActivity failed to get location")
        }*/
}