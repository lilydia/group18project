package com.uwaterloo.watodo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ReminderService {
    private List<Geofence> geofenceList = new ArrayList<>();
    private final static int GEOFENCE_RADIUS_IN_METERS = 300;
    private final static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    private PendingIntent mGeofencePendingIntent;


    public ReminderService() {
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> g) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(g)
                .build();
    }

    private PendingIntent getGeofencePendingIntent(Context context) {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public void newLocationReminder(final double latitude, final double longitude, String taskTitle) {
        Log.i("ReminderService", "newLocationReminder");
        if (latitude != 0) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(taskTitle)
                    .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(
                            Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT |
                                    Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(1)
                    .setNotificationResponsiveness(2000)
                    .build();
            geofenceList.add(geofence);
            MainActivity.geofencingClient.addGeofences(getGeofencingRequest(geofenceList), getGeofencePendingIntent(MainActivity.getAppContext()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // do something
                            Log.i("TaskViewModel", "Geofence successfully created." + " Lat: " + latitude + " Long: " + longitude);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // do something
                            Log.e("TaskViewModel", e.getMessage());
                        }
                    });
        }
    }
}
