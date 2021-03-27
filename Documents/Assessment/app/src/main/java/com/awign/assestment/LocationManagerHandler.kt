package com.awign.assestment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat


class LocationManagerHandler (private val context: Context,private val  locationCallBack: LocationChangeListener):LocationListener{


    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    // location
    var location: Location? = null

    // latitude
    var latitude = 0.0

    // longitude
    var longitude = 0.0

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10.0f // 10 meters

    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = 1000   // 1 minute

    // Declaring a Location Manager

    private var locationManager: LocationManager

    init {



        locationCallBack.showProgressBar(true)
        locationManager =  context.getSystemService(Context.LOCATION_SERVICE)  as LocationManager



        getLocation()

    }



    @SuppressLint("MissingPermission")
    fun getLocation(){


        if(hasPermission()){



        try {
            locationManager = context
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled


                Log.d("Network", "no network  available")
            } else {

                if (isNetworkEnabled) {

                    locationCallBack.showProgressBar(false)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES.toLong(),MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),this)

                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        Log.d("Network", "Networksss")
                        location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            Log.d("Network", "Networkssss333")
                            locationCallBack.showProgressBar(false)
                            locationCallBack.onLocationChange(location)

//                            latitude = location!!.latitude
//                            longitude = location!!.longitude
//                            Log.d(
//                                "Network1",
//                                getLatitude().toString() + "----" + getLongitude().toString()
//                            )
                        }
                    } else {
                        Log.d("Network", "Networkyyyy")
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                           MIN_TIME_BW_UPDATES.toLong(),
                           MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location =
                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                locationCallBack.showProgressBar(false)
                                locationCallBack.onLocationChange(location)
//                                latitude = location!!.latitude
//                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }



        }else{
            Toast.makeText(context,"Not getting proper Permission",Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }
    }


    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}





    private fun hasPermission():Boolean{

        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onLocationChanged(location: Location) {
        locationCallBack.showProgressBar(false)
        locationCallBack.onLocationChange(location)

    }

}
