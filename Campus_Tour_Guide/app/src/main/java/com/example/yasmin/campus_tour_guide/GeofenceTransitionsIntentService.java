package com.example.yasmin.campus_tour_guide;

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


/**
 * Listener for geofence transition changes
 * <p/>
 * Receives geofence transition events from Location Services in form of Intent
 * containing transition type and geofence id(s) that have triggered the transition.
 * Creates notification as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

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
    /**
     * Definition of Intent: Message passed between components
     **/
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            // Get the transition type, either: entry or exit
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            switch(geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    //Toast toast = Toast.makeText(this, "It worked, you're at home", Toast.LENGTH_SHORT);
                    //toast.show();
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    //toast = Toast.makeText(this, "You've left", Toast.LENGTH_SHORT);
                    //toast.show();
                    break;
                default:
                   // toast = Toast.makeText(this, "Unknown geofence", Toast.LENGTH_SHORT);
                   // toast.show();
            }
            sendNotification(this, "Home", "It worked, you're at home");
        }
    }

    private void sendNotification(Context context, String notificationText, String notificationTitle) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.home)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                //.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        //Builds notification and issues it
        notificationManager.notify(0,notificationBuilder.build());

    }
    /*
    private String getTriggeringGeofences(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

        String[]
    }
    */
}



