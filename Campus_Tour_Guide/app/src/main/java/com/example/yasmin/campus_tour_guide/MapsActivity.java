package com.example.yasmin.campus_tour_guide;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap campusMap; //Google Map Object
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";
    ArrayList<Geofence> mGeofenceList; //List of geofences used
    ArrayList<String> mGeofenceNames; //List of geofence names
    ArrayList<LatLng> mGeofenceCoordinates; //List of geofence coordinates
    public GeofenceStore mGeofenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Build geofences that will be used (tour stops)
        populateGeofences();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        //mGeofenceStore.disconnect();
        super.onStop();
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
                .position(new LatLng(51.522352, -0.042789)).title("Stepney Green Entrance"));
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522723, -0.042991)).title("ITL"));
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522640, -0.041203)).title("Engineering Building"));

        campusMap.moveCamera(CameraUpdateFactory.newLatLng(point)); //Moves map according to the update with an animation

    }

    /**This method contains geofence data, all of the tour stops**/

    public void populateGeofences() {

        //Empty list for storing geofences
        mGeofenceNames = new ArrayList<String>();
        mGeofenceCoordinates = new ArrayList <LatLng>();
        //mGeofenceRadius = new ArrayList<Integer>(); --Not needed for now
        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceNames.add("Home");
        mGeofenceNames.add("Stepney Green Entrance");
        mGeofenceNames.add("ITL");
        mGeofenceNames.add("Engineering Building");

        mGeofenceCoordinates.add(new LatLng(51.557935, 0.002336));
        mGeofenceCoordinates.add(new LatLng(51.522352, -0.042789));
        mGeofenceCoordinates.add(new LatLng(51.522723, -0.042991));
        mGeofenceCoordinates.add(new LatLng(51.522640, -0.041203));


        for(int i=0; i<mGeofenceNames.size(); i++) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(mGeofenceNames.get(i))
                            //(latitude, longitude, radius_in_meters)
                    .setCircularRegion(mGeofenceCoordinates.get(i).latitude, mGeofenceCoordinates.get(i).longitude, 50)
                            //expiration in milliseconds
                    .setExpirationDuration(300000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
            //Add geofences to GeofenceStore obect
            mGeofenceStore = new GeofenceStore(this, mGeofenceList); //Send over context and geofence list

    }
}



