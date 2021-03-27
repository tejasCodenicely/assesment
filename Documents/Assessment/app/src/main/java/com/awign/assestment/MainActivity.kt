package com.awign.assestment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener,GoogleMap.OnMarkerDragListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener,
    LocationChangeListener {


 companion object{




 }

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var googleMap: GoogleMap

    private lateinit var snackbar: Snackbar



    private lateinit var geoFenceLimit:Circle

    private lateinit var marker:Marker


    private lateinit var geoFenceMarker:Marker

    private lateinit var  geoFenceHelperClass: GeoFenceHelperClass


    private lateinit var alertDialog: AlertDialog

    private var geoFencingAlreadyAdded = false

    private var isLocationSet = false


    private val CURRENT_LOCATION = "current_location"


    //default Value
     private var accuracyRadius:Double  = 100.0




    private lateinit var  currentLocationLatLng: LatLng






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSnakeBar("assa",false)



        mapInit(savedInstanceState)


//        initLocation()

        geoFenceHelperClass = GeoFenceHelperClass(this)
    }

    private fun initLocation(){
        if(!hasPermission() && !checkGPSStatus(this)){


                initPermission()




        }else{

             if(!checkGPSStatus(this)){
                 askToEnableGprs()
             }else{
                 Log.d("check","initiate")
                 initCurrentLocation()


             }

        }
    }



    override fun onStart() {

        initLocation()



        super.onStart()
    }

    override fun onRestart() {

        super.onRestart()
    }

    private fun mapInit(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(resources.getString(R.string.map_api_key))
        }




        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)



    }






    private fun initCurrentLocation(){


        LocationHelper(this,this)




    }

    fun checkGPSStatus(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    private fun askToEnableGprs() {


            alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle("Location Permission")
            alertDialog.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.")
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            })
            alertDialog.setCancelable(false)
            alertDialog.show()


            if (!alertDialog.isShowing()) {
                alertDialog.show()
            }


    }




   override fun onResume() {

        mapView.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        this.googleMap = googleMap!!

        val uiSettings = googleMap!!.uiSettings
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isZoomGesturesEnabled = true
        uiSettings.isMyLocationButtonEnabled = true

        googleMap.setOnCameraMoveStartedListener(this)
        googleMap.setOnCameraMoveCanceledListener(this)
        googleMap.setOnCameraIdleListener(this)



        if(hasPermission()){
            googleMap.isMyLocationEnabled = true

        }
//
//        googleMap.ong
        googleMap.setOnMarkerDragListener(this)
        googleMap.setOnMapClickListener(this)




    }




    private fun setCurrentLocation(lat :Double,lng:Double,accuracy: Float) {

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 19f)
        googleMap?.let {
            it.animateCamera(cameraUpdate)
        }



    }


    private fun addMarker(lat :Double,lng:Double){

        val markerOptions = MarkerOptions()
         markerOptions.draggable(true).title("current Location").position(LatLng(lat,lng))
         marker =  googleMap.addMarker(markerOptions)





//        initGeoFencing()

    }

    @SuppressLint("MissingPermission")
    private fun initGeoFencing(lat :Double,lng:Double,accuracy: Float){
        geoFencingAlreadyAdded = true
        val client = LocationServices.getGeofencingClient(this)
        val geoFence = geoFenceHelperClass.getGeoFence(LatLng(lat, lng),(accuracy),CURRENT_LOCATION)
        val geoFenceRequest = geoFenceHelperClass.createGeoRequest(geoFence)
        val pendingIntent = geoFenceHelperClass.geoFencingIntent()

        if(hasPermission()){
            client.addGeofences(geoFenceRequest,pendingIntent)
                    .addOnSuccessListener {

                        Snackbar.make(mainView, "Success",Snackbar.LENGTH_LONG).show()

                    }.addOnFailureListener {

                    Log.d("checkException",it.message.toString()+ it.localizedMessage)

                        Snackbar.make(mainView,  GeofenceErrorMessages.getErrorString(this,it),Snackbar.LENGTH_LONG).show()

                    }
        }



    }


    private fun addCircle(position:LatLng,accuracy: Float){
        val circleOptions = CircleOptions()
                                     .center(position)
                                     .strokeColor(Color.argb(255,255,0,0))
                                      .fillColor(Color.argb(64,255,0,0))
                                       .radius((accuracy).toDouble())
        circleOptions.strokeWidth((4).toFloat())
        geoFenceLimit =  googleMap.addCircle(circleOptions)


    }





    override fun onCameraMoveStarted(p0: Int) {

    }

    override fun onCameraMoveCanceled() {

    }

    override fun onCameraIdle() {
    }





    private fun initSnakeBar(message: String,always:Boolean){
        if(always){
            snackbar =  Snackbar.make(mainView, message, Snackbar.LENGTH_INDEFINITE);
        }else{
            snackbar= Snackbar.make(mainView, message, Snackbar.LENGTH_LONG)
        }



    }

    override fun onMarkerDragEnd(dragEnd: Marker?) {
        checkForGeoFenceEntry(dragEnd!!.position,currentLocationLatLng.latitude,currentLocationLatLng.longitude, accuracyRadius)
    }

    override fun onMarkerDragStart(dragStart: Marker?) {

    }

    override fun onMarkerDrag(onMarkerDrag: Marker?) {



    }

    fun checkForGeoFenceEntry(dragLocation: LatLng, geofenceLat: Double, geofenceLong: Double, radius: Double) {
        val startLatLng = LatLng(dragLocation.latitude, dragLocation.longitude) // User Location
        val geofenceLatLng = LatLng(geofenceLat, geofenceLong) // Center of geofence

        val distanceInMeters = SphericalUtil.computeDistanceBetween(startLatLng, geofenceLatLng)

        if (distanceInMeters < radius) {


            Toast.makeText(this,"You are Inside the Radius",Toast.LENGTH_LONG).show()

        }else{

            Toast.makeText(this,"You cannot point outside the radius",Toast.LENGTH_LONG).show()
            marker.remove()
            val markerOptions = MarkerOptions()
            markerOptions.draggable(true).title("current Location").position(currentLocationLatLng)
            marker =  googleMap.addMarker(markerOptions)


        }
    }

    override fun onMarkerClick(markerClicked: Marker?): Boolean {

        return true
    }

    override fun onMapLongClick(p0: LatLng?) {



    }

    override fun onMapClick(p0: LatLng?) {


    }


    fun isGPSEnabled(mContext: Context): Boolean {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }



    private fun hasPermission():Boolean{

        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }


    private fun initPermission(){
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {

                        if(checkGPSStatus(this@MainActivity)){
                            initCurrentLocation()
                        }else{
                            askToEnableGprs()
                        }

                    }else{
                        Toast.makeText(applicationContext,"Get Permission",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()



    }

    override fun onLocationChange(location: Location?) {




        if (location?.latitude != null && location.longitude != null) {
            if (!isLocationSet) {

                this.accuracyRadius = location?.accuracy!!.toDouble()
                currentLocationLatLng = LatLng(location.latitude, location.longitude)


                isLocationSet = true
                setCurrentLocation(
                    location.latitude,
                    location.longitude,
                    (accuracyRadius).toFloat()
                )
                addMarker(location.latitude, location.longitude)
                addCircle(LatLng(location.latitude, location.longitude), (accuracyRadius).toFloat())
                if (!geoFencingAlreadyAdded) {
                    initGeoFencing(
                        location.latitude,
                        location.longitude,
                        (accuracyRadius).toFloat()
                    )
                }
            }
        }
    }

    override fun showProgressBar(boolean: Boolean) {

        if(snackbar!=null){
            if(boolean){
                initSnakeBar("fetching current location....",true)
                snackbar.show()
            }else{
                if(snackbar.isShown){
                    snackbar.dismiss()
                }
            }

        }
    }



}
