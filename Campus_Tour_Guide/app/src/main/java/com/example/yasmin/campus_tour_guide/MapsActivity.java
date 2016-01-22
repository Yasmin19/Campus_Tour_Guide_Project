package com.example.yasmin.campus_tour_guide;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ResultCallback<Status> {

    private GoogleMap campusMap; //Google Map Object
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";
    protected ArrayList<Geofence> mGeofenceList; //List of geofences used
    private PendingIntent mGeofencePendingIntent; //Used when requesting to add or remove geofences ---- MIGHT NOT NEED


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Empty list for storing geofences
        mGeofenceList = new ArrayList<Geofence>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Get geofences that will be used (tour stops)
        populateGeofences();

        //Add geofences


        /* Program crashes at this point*/
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
        /*end of problem*/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        campusMap = googleMap;
        campusMap.setMyLocationEnabled(true); //Enable My Location Layer

        //Get latitude and longitude of current location
        LatLng point = new LatLng(51.557935, 0.002336);
        campusMap.addMarker(new MarkerOptions().position(point).title("Current Position"));
        //Add new marker for entrance
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522352, -0.042789)).title("Entrance"));
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522723, -0.042991)).title("ITL"));

        campusMap.moveCamera(CameraUpdateFactory.newLatLng(point)); //Moves map according to the update with an animation

    }

    /**This method contains geofence data, all of the tour stops**/

    public void populateGeofences() {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Home")

                        //(latitude, longitude, radius_in_meters)
                .setCircularRegion(51.557935, 0.002336, 10000) //Home
                        //.setCircularRegion(51.522693, -0.041864, 100) //Electronics Lab
                        //expiration in milliseconds
                .setExpirationDuration(300000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

    }


    /***Specify geofences to monitor and set how geofence events are triggered***/

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        //INITIAL_TRIGGER_ENTER flag indicates geofencing service should trigger
        //GEOFENCE_TRANSITION_ENTER notification when geofence added if device already inside geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        /*NOTE: INITIAL_TRIGGER_DWELL can also be used, triggers only when user stops for certain amount of time within geofence - help reduce alert spam*/

        //Add geofences to be monitored by geofencing service
        builder.addGeofences(mGeofenceList);

        //Return a GeofencingRequest
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void removeGeofences() {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        );
    }

    //Runs when result of calling addGeofences() and removeGeofences() becomes available
    public void onResult(Status status) {
        Toast toast = Toast.makeText(this, "Added Geofences", Toast.LENGTH_SHORT);
        toast.show();
    }

}



