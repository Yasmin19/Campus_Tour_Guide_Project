package com.example.yasmin.campustourguide;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 *
 */
public class GeofenceStore implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>, LocationListener {

    private final String TAG = "GeofenceTransitionsIS";
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Geofence> mGeofences;
    private GeofencingRequest mGeofencingRequest;
    private PendingIntent mGeofencePendingIntent; //Used when requesting to add or remove geofences

    public GeofenceStore(Context context, ArrayList<Geofence> geofences) {
        mContext = context;
        mGeofences = new ArrayList<Geofence>(geofences);
        mGeofencePendingIntent = null;

        // Build a new GoogleApiClient, specify that we want to use LocationServices
        // by adding the API to the client, specify the connection callbacks are in
        // this class as well as the OnConnectionFailed method.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    //Runs when result of calling addGeofences() and removeGeofences() becomes available
    @Override
    public void onResult(Status result) {
        //this works
        if (result.isSuccess()) {
            Log.v(TAG, "Success!");
            Toast toast = Toast.makeText(mContext, "Added Geofences", Toast.LENGTH_SHORT);
            toast.show();
        } else if (result.hasResolution()) {
            // TODO Handle resolution
        } else if (result.isCanceled()) {
            Log.v(TAG, "Canceled");
        } else if (result.isInterrupted()) {
            Log.v(TAG, "Interrupted");
        } else {

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed.");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    /*Build a GeofencingRequest and add geofences that we want to monitor */

        //Currently conected, need to create Geofecning Request with geofences we have stored
        mGeofencingRequest = new GeofencingRequest.Builder().addGeofences(
                mGeofences).build();

        mGeofencePendingIntent = createRequestPendingIntent();

        // Submitting the request to monitor geofences.
        PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient, mGeofencingRequest,
                        mGeofencePendingIntent);

        // Set the result callbacks listener to this class.
        pendingResult.setResultCallback(this);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "Connection suspended.");
    }


    @Override
    public void onLocationChanged(Location location) {

    }


    private PendingIntent createRequestPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
