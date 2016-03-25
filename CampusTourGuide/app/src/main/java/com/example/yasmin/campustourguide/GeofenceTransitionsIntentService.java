package com.example.yasmin.campustourguide;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.Map;


/**
 * Listener for geofence transition changes
 * <p/>
 * Receives geofence transition events from Location Services in form of Intent
 * containing transition type and geofence id(s) that have triggered the transition.
 * Creates notification as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceIntentIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**Handles incoming intents**/
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            // Get the transition type, either: entray or exit
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            String title = "";

            switch(geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    title = "Geofence Entered";
                    break;
                default:
                    title = "Unknown";
            }
            sendNotification(this, getTriggeredGeofence(intent), title);
        }
    }

    private void sendNotification(Context context, String notificationText, String notificationTitle) {

        Log.d("Geofence", "Geofence detected!");
        getRouting(notificationText);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.queenmary)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        //Builds notification and issues it
        notificationManager.notify(0,notificationBuilder.build());



    }

    private String getTriggeredGeofence(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

        //Create array containing id's size of geofences list
        String[] geofenceIds = new String[geofences.size()];

        for(int i=0; i<geofences.size(); i++) {
            geofenceIds[i] = geofences.get(i).getRequestId();
        }

        return TextUtils.join(", ", geofenceIds); //return id's of geofence(s) seperated by commas
    }

    private void getRouting(String tourStop){

        int angle = 0;

        if (tourStop.contains("Santander Bank")){}
        // MapsActivity.esc = true;


        //End previous threads and start new IOIO thread
        //Send over degrees, in IOIO_OTG figure out amount of movement needed


        }

}






