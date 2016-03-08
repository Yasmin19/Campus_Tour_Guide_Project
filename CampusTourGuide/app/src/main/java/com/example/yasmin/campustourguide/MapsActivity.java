package com.example.yasmin.campustourguide;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.Object;

import java.util.ArrayList;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap campusMap; //Google Map Object
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";
    ArrayList<Geofence> mGeofenceList; //List of geofences used
    ArrayList<String> mGeofenceNames; //List of geofence names
    ArrayList<LatLng> mGeofenceCoordinates; //List of geofence coordinates
    public GeofenceStore mGeofenceStore;
    public static EditText distanceField;
    public static ToggleButton button_;
    public static Button start;

    private static final LatLng MAYNARD_HOUSE = new LatLng(51.525095, -0.039004);
    //private static final LatLng VAREY_CURVE = new LatLng(51.525355, -0.039331);
    private static final LatLng VILLAGE_BEAUMONT = new LatLng(51.525579, -0.039499);
    private static final LatLng SANTANDER = new LatLng(51.526144, -0.039733);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(this, IOIO_OTG.class));

        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Build geofences that will be used (tour stops)
        populateGeofences();
        test();


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

        campusMap.addMarker(new MarkerOptions()
                .position(MAYNARD_HOUSE).title("Maynard House"));
        // campusMap.addMarker(new MarkerOptions()
        //        .position(VAREY_CURVE).title("Varey House/The Curve"));
        campusMap.addMarker(new MarkerOptions()
                .position(VILLAGE_BEAUMONT).title("Village Shop/Beaumont Court"));
        campusMap.addMarker(new MarkerOptions()
                .position(SANTANDER).title("Santander Bank"));

        addRoute();
        campusMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILLAGE_BEAUMONT, 17)); //Moves map according to the update with an animation

        distanceField = (EditText) findViewById(R.id.distanceField);
        button_ = (ToggleButton) findViewById(R.id.button);
        start = (Button) findViewById(R.id.start);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();

        //Register the listener with Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

    }

    private void addRoute(){

        campusMap.addPolyline((new PolylineOptions())
                .add(MAYNARD_HOUSE, new LatLng(51.525384, -0.039367),
                        VILLAGE_BEAUMONT, SANTANDER)
                .width(15)
                .color(Color.BLUE)
                .geodesic(true));

        Location loc = new Location("Src");
        loc.setLongitude(51.557912);
        loc.setLatitude(0.002327);

        Location dest = new Location("Dest");
        dest.setLatitude(51.525095);
        dest.setLongitude(-0.039004);

        float degree = loc.bearingTo(dest);
        Log.d("Bearing w/l", degree + " degrees");

    }
    /**This method contains geofence data, all of the tour stops**/

    public void populateGeofences() {

        //Empty list for storing geofences
        mGeofenceNames = new ArrayList<String>();
        mGeofenceCoordinates = new ArrayList <LatLng>();
        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceNames.add("Maynard House");
        //mGeofenceNames.add("Varey House/The Curve");
        mGeofenceNames.add("Village Shop/Beaumont Court");
        mGeofenceNames.add("Santander Bank");

        mGeofenceCoordinates.add(MAYNARD_HOUSE);
        //mGeofenceCoordinates.add(VAREY_CURVE);
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

    public void route() {
        ArrayList<LatLng> mRoute = new ArrayList <LatLng>();

        mRoute.add(new LatLng(51.524855, -0.038531)); //Starting Point
        mRoute.add(new LatLng(51.524905, -0.038653)); //10m
        mRoute.add(new LatLng(51.524957, -0.038774)); //20m
        mRoute.add(new LatLng(51.525015, -0.038886)); //30m
        mRoute.add(new LatLng(51.525081, -0.038986)); //40m
        mRoute.add(new LatLng(51.525149, -0.039074)); //50m
        mRoute.add(new LatLng(51.525221, -0.039164)); //60m
        mRoute.add(new LatLng(51.525295, -0.039247)); //70m
    }

    public void test() {

        Location loc1 = new Location("Loc1");
        Location loc2 = new Location("Loc2");
        Location loc3 = new Location("Loc3");

        Location loc4 = new Location("Loc4");
        Location loc5 = new Location("Loc5");

        loc1.setLatitude(51.524855);
        loc1.setLongitude(-0.038531);

        loc2.setLatitude(51.524905);
        loc2.setLongitude(-0.038653);

        loc3.setLatitude(51.524957);
        loc3.setLongitude(-0.038774);

        loc4.setLatitude(51.525850);
        loc4.setLongitude(-0.039620);

        loc5.setLatitude(51.526060);
        loc5.setLatitude(-0.039700);


        Log.d("Bea 1", "" + loc1.bearingTo(loc2));
        Log.d("Distance", "" + loc1.distanceTo(loc2));
        Log.d("Bea 1 inv", "" + loc2.bearingTo(loc1));
        Log.d("Bea 2", "" + loc2.bearingTo(loc3));
        Log.d("Bea 3", "" + loc4.bearingTo(loc5));

    }

}



