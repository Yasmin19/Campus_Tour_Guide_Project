package com.example.yasmin.campus_tour_guide;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap campusMap; //Google Map Object
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";
    ArrayList<Geofence> mGeofenceList; //List of geofences used
    ArrayList<String> mGeofenceNames; //List of geofence names
    ArrayList<LatLng> mGeofenceCoordinates; //List of geofence coordinates
    public GeofenceStore mGeofenceStore;

    private static final LatLng MAYNARD_HOUSE = new LatLng(51.525103, -0.039015);
    private static final LatLng VAREY_CURVE = new LatLng(51.525355, -0.039331);
    private static final LatLng VILLAGE_BEAUMONT = new LatLng(51.525656, -0.039534);
    private static final LatLng SANTANDER = new LatLng(51.526163, -0.039740);



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

        //Add new marker for entrance
        /*
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522352, -0.042789)).title("Stepney Green Entrance"));
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522723, -0.042991)).title("ITL"));
        campusMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.522640, -0.041203)).title("Engineering Building"));
        */
        campusMap.addMarker(new MarkerOptions()
                .position(MAYNARD_HOUSE).title("Maynard House"));
        campusMap.addMarker(new MarkerOptions()
                .position(VAREY_CURVE).title("Varey House/The Curve"));
        campusMap.addMarker(new MarkerOptions()
                .position(VILLAGE_BEAUMONT).title("Village Shop/Beaumont Court"));
        campusMap.addMarker(new MarkerOptions()
                .position(SANTANDER).title("Santander Bank"));

        addRoute();
        campusMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILLAGE_BEAUMONT,17)); //Moves map according to the update with an animation

    }

    private void addRoute(){

        campusMap.addPolyline((new PolylineOptions())
                .add(MAYNARD_HOUSE, VAREY_CURVE, VILLAGE_BEAUMONT, SANTANDER)
                .width(5)
                .color(Color.BLUE)
                .geodesic(true));

    }
    /**This method contains geofence data, all of the tour stops**/

    public void populateGeofences() {

        //Empty list for storing geofences
        mGeofenceNames = new ArrayList<String>();
        mGeofenceCoordinates = new ArrayList <LatLng>();
        //mGeofenceRadius = new ArrayList<Integer>(); --Not needed for now
        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceNames.add("Maynard House");
        mGeofenceNames.add("Varey House/The Curve");
        mGeofenceNames.add("Village Shop/Beaumont Court");
        mGeofenceNames.add("Santander Bank");

        mGeofenceCoordinates.add(MAYNARD_HOUSE);
        mGeofenceCoordinates.add(VAREY_CURVE);
        mGeofenceCoordinates.add(VILLAGE_BEAUMONT);
        mGeofenceCoordinates.add(SANTANDER);


        for(int i=0; i<mGeofenceNames.size(); i++) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(mGeofenceNames.get(i))
                            //(latitude, longitude, radius_in_meters)
                    .setCircularRegion(mGeofenceCoordinates.get(i).latitude, mGeofenceCoordinates.get(i).longitude, 15)
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



