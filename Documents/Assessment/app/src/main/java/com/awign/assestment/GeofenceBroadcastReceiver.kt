package com.awign.assestment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("checkLogReceiving", "Receiving")

        // an Intent broadcast.
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();



        val geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
//            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        val geofenceList = geofencingEvent.triggeringGeofences;
        for ( geofence in geofenceList) {
//            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        val transitionType = geofencingEvent.geofenceTransition;



        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER->{
                Toast.makeText(context, "USER ENTER GEO FENCING", Toast.LENGTH_LONG).show();
            }


            Geofence.GEOFENCE_TRANSITION_DWELL->{
                Toast.makeText(context, "GEOFENCE TRANSITION DWELL", Toast.LENGTH_LONG).show();
            }



            Geofence.GEOFENCE_TRANSITION_EXIT->{
                Toast.makeText(context, "USER EXIT GEOFENCING", Toast.LENGTH_LONG).show();
            }



        }

    }
}