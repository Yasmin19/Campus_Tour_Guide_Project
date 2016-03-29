package com.example.yasmin.campustourguide;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.Object;

import java.util.ArrayList;

import ioio.lib.util.android.IOIOActivity;
import ioio.lib.util.android.IOIOService;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap campusMap; //Google Map Object
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";

    ArrayList<Geofence> mGeofenceList; //List of geofences used
    ArrayList<String> mGeofenceNames; //List of geofence names
    ArrayList<LatLng> mGeofenceCoordinates; //List of geofence coordinates
    public GeofenceStore mGeofenceStore;

    //public static EditText distanceField;
    public static EditText left;
    public static EditText right;
    public static Button start;
    public static Button flagButton;
    public static Button startGeofences;
    public static boolean esc = false;
    public static int angleLeft = 0;
    public static int angleRight = 0;
    public Intent intent;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private static final LatLng MAYNARD_HOUSE = new LatLng(51.525095, -0.039004);
    //private static final LatLng VAREY_CURVE = new LatLng(51.525355, -0.039331);
    private static final LatLng VILLAGE_BEAUMONT = new LatLng(51.525579, -0.039499);
    private static final LatLng SANTANDER = new LatLng(51.526183, -0.039749);

    public static Location[] locName = new Location[5];
    public static float[] locBearing = new float[4];
    public static float[] locDistance = new float[4];

    public static double azimuthLPF=-1;
    int alpha=10;
    public static double azimuthDeg;
    public static double azimuth360;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //startService(new Intent(getApplicationContext(), MyOrientationListener.class));
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
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        campusMap = googleMap;
        campusMap.setMyLocationEnabled(true); //Enable My Location Layer
        campusMap.getUiSettings().setCompassEnabled(true);

        campusMap.addMarker(new MarkerOptions()
                .position(MAYNARD_HOUSE).title("Maynard House"));
        // campusMap.addMarker(new MarkerOptions()
        //        .position(VAREY_CURVE).title("Varey House/The Curve"));
        campusMap.addMarker(new MarkerOptions()
                .position(VILLAGE_BEAUMONT).title("Village Shop/Beaumont Court"));


        campusMap.addMarker(new MarkerOptions()
                .position(SANTANDER).title("Santander Bank")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.santander_marker)));

        addRoute();
        distances();

        campusMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILLAGE_BEAUMONT, 17)); //Moves map according to the update with an animation

        //distanceField = (EditText) findViewById(R.id.distanceField);
        //button_ = (ToggleButton) findViewById(R.id.button);
        start = (Button) findViewById(R.id.start);
        flagButton = (Button) findViewById(R.id.flagButton);
        startGeofences = (Button) findViewById(R.id.startGeofences);


        start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Button start = (Button) arg0;

                angleLeft = Integer.valueOf(left.getText().toString());
                angleRight = Integer.valueOf(right.getText().toString());


                intent = new Intent(mContext, IOIO_OTG.class);
                //intent.putExtra("pwm", "hello");
                startService(intent);

                //populateGeofences();

                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                startService(new Intent(getApplicationContext(), MyOrientationListener.class));

            }
        });

        startGeofences.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Button start = (Button) arg0;

                    populateGeofences();

            }
        });
    }


    private void addRoute() {

        Location loc = new Location("Src");
        loc.setLongitude(51.557912);
        loc.setLatitude(0.002327);

        Location dest = new Location("Dest");
        dest.setLatitude(51.525095);
        dest.setLongitude(-0.039004);

        float degree = loc.bearingTo(dest);
        Log.d("Bearing w/l", degree + " degrees");

        ArrayList<LatLng> mRoute = new ArrayList<LatLng>();
        ArrayList<LatLng> mRoute2 = new ArrayList<LatLng>();


        mRoute.add(new LatLng(51.524855, -0.038531)); //Starting Point
        mRoute.add(new LatLng(51.524905, -0.038653)); //10m
        mRoute.add(new LatLng(51.524957, -0.038774)); //20m
        mRoute.add(new LatLng(51.525015, -0.038886)); //30m
        mRoute.add(new LatLng(51.525081, -0.038986)); //40m
        mRoute.add(new LatLng(51.525149, -0.039074)); //50m
        mRoute.add(new LatLng(51.525221, -0.039164)); //60m
        mRoute.add(new LatLng(51.525295, -0.039247)); //70m


        mRoute2.add(new LatLng(51.526590, -0.039799)); //Starting Point - France House
        mRoute2.add(new LatLng(51.526569, -0.039912)); //Turning Point
        mRoute2.add(new LatLng(51.526144, -0.039733)); //Santander Bank

        campusMap.addPolyline((new PolylineOptions())
                .add(mRoute.get(0), mRoute.get(1), mRoute.get(2), mRoute.get(3),
                        mRoute.get(4), mRoute.get(5), mRoute.get(6), mRoute.get(7),
                        VILLAGE_BEAUMONT, SANTANDER)
                .width(15)
                .color(Color.BLUE)
                .geodesic(true));

        campusMap.addPolyline((new PolylineOptions())
                .add(mRoute2.get(0), mRoute2.get(1), mRoute2.get(2))
                .width(15)
                .color(Color.RED)
                .geodesic(true));

    }


    /**
     * This method contains geofence data, all of the tour stops
     **/

    public void populateGeofences() {

        //Empty list for storing geofences
        mGeofenceNames = new ArrayList<String>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceNames.add("Maynard House");
        //mGeofenceNames.add("Varey House/The Curve");
        //mGeofenceNames.add("Village Shop/Beaumont Court");
        mGeofenceNames.add("Santander Bank");
        mGeofenceNames.add("Canalside");


        mGeofenceCoordinates.add(MAYNARD_HOUSE);
        //mGeofenceCoordinates.add(VAREY_CURVE);
        //mGeofenceCoordinates.add(VILLAGE_BEAUMONT);
        mGeofenceCoordinates.add(SANTANDER);
        mGeofenceCoordinates.add(new LatLng(51.526143, -0.039552));


        for (int i = 0; i < mGeofenceNames.size();i++){
        mGeofenceList.add(new Geofence.Builder()
        // Set the request ID of the geofence. This is a string to identify this
        // geofence.
        .setRequestId(mGeofenceNames.get(i))
        //(latitude, longitude, radius_in_meters)
        .setCircularRegion(mGeofenceCoordinates.get(i).latitude,mGeofenceCoordinates.get(i).longitude,30)
        //expiration in milliseconds
        .setExpirationDuration(300000000)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }
        //Add geofences to GeofenceStore obect
        mGeofenceStore = new GeofenceStore(this, mGeofenceList); //Send over context and geofence list

    }

    public void distances(){

        locName[0] = new Location("France House");
        locName[0].setLatitude(51.526590);
        locName[0].setLongitude(-0.039812);

        locName[1] = new Location("Turning Point");
        locName[1].setLatitude(51.526577);
        locName[1].setLongitude(-0.039903);

        locName[2] = new Location("Mid Point");
        locName[2].setLatitude(51.526340);
        locName[2].setLongitude(-0.039812);

        locName[3] = new Location("Turning Canal");
        locName[3].setLatitude(51.526117);
        locName[3].setLongitude(-0.039722);

        locName[4] = new Location("Canal Side");
        locName[4].setLatitude(51.526142);
        locName[4].setLongitude(-0.039536);

        locBearing[0] = locName[0].bearingTo(locName[1]);
        locDistance[0] = locName[0].distanceTo(locName[1]);
        //Log.v("B/D", "FRANCE HOUSE - TURNING POINT -- Bearing: " + locBearing[0] + ", Distance: " + locDistance[0]);

        locBearing[1] = locName[1].bearingTo(locName[2]);
        locDistance[1] = locName[1].distanceTo(locName[2]);
        //Log.v("B/D", "TURNING POINT - MID-POINT -- Bearing: " +  locBearing[1] + ", Distance: " + locDistance[1]);

        locBearing[2] = locName[2].bearingTo(locName[3]);
        locDistance[2] = locName[1].distanceTo(locName[2]);
        //Log.v("B/D", "MID-POINT - TURNING CANAL -- Bearing: " +  locBearing[1] + ", Distance: " + locDistance[1]);


        locBearing[3] = locName[3].bearingTo(locName[4]);
        locDistance[3] = locName[3].distanceTo(locName[4]);
       // Log.v("B/D", "TURNING CANAL - CANAL SIDE -- Bearing: " + locBearing[2] + ", Distance: " + locDistance[2]);


    }

}


