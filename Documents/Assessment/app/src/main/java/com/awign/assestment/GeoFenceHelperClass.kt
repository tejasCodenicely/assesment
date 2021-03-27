package com.awign.assestment

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class GeoFenceHelperClass(private val context:Context){



   private  val pendingIntent: PendingIntent

  private  val intent:Intent = Intent(context,GeofenceBroadcastReceiver::class.java)

    init {


        pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)


      }



  fun createGeoRequest(geofence: Geofence): GeofencingRequest {


        /// TO:DO can add multiple

        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()


    }

    public fun getGeoFence(position: LatLng?, radius: Float, id:String): Geofence {


        Log.d("checkLocation","${position!!.latitude}---${position!!.longitude}")

        return Geofence.Builder()
            .setRequestId("myGeoFence")
            .setCircularRegion(
                position!!.latitude,
                position!!.longitude,
                radius)
            .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

    }






     fun geoFencingIntent():PendingIntent{

        return pendingIntent

    }

}