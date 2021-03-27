package com.awign.assestment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


@SuppressLint("MissingPermission")
class LocationHelper(private val context: Context, locationChangeListener: LocationChangeListener){


    private  val fusedLocationProviderClient:FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {

        locationChangeListener.showProgressBar(true)

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            locationChangeListener.onLocationChange(it)



        }



    }


    private fun hasPermission():Boolean{

        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}



