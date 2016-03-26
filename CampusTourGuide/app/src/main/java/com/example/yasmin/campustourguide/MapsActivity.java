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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener{

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
    private static final LatLng SANTANDER = new LatLng(51.526144, -0.039733);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext=this;
        //startService(new Intent(this, IOIO_OTG.class));
       // startService(new Intent(this, MyOrientationListener.class));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Build geofences that will be used (tour stops)



        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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
    protected void onDestroy(){
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

        campusMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VILLAGE_BEAUMONT, 17)); //Moves map according to the update with an animation

        //distanceField = (EditText) findViewById(R.id.distanceField);
        //button_ = (ToggleButton) findViewById(R.id.button);
        start = (Button) findViewById(R.id.start);
        flagButton = (Button) findViewById(R.id.flagButton);
        startGeofences = (Button) findViewById(R.id.startGeofences);
        left = (EditText) findViewById(R.id.left);
        right = (EditText) findViewById(R.id.right);

        right.setText("0");
        left.setText("0");


        start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Button start = (Button) arg0;

                angleLeft = Integer.valueOf(left.getText().toString());
                angleRight = Integer.valueOf(right.getText().toString());

                Log.v("Angle", "" + angleLeft);
                Log.v("Angle", "" + angleRight);

                //

                /*
                //Register the listener with Location Manager to receive location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }*/

                intent = new Intent(mContext, IOIO_OTG.class);
                //intent.putExtra("pwm", "hello");
                startService(intent);

                //populateGeofences();


               // LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
               // LocationListener mlocListener = new MyLocationListener();
                //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            }
        });

        startGeofences.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Button start = (Button) arg0;

                populateGeofences();

            }
        });
    }



    private void addRoute(){

        Location loc = new Location("Src");
        loc.setLongitude(51.557912);
        loc.setLatitude(0.002327);

        Location dest = new Location("Dest");
        dest.setLatitude(51.525095);
        dest.setLongitude(-0.039004);

        float degree = loc.bearingTo(dest);
        Log.d("Bearing w/l", degree + " degrees");

        ArrayList<LatLng> mRoute = new ArrayList <LatLng>();
        ArrayList<LatLng> mRoute2 = new ArrayList <LatLng>();


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
        //mGeofenceNames.add("France House");
        mGeofenceNames.add("Turning Point");
        mGeofenceNames.add("Canalside");
        mGeofenceNames.add("Home");
        mGeofenceNames.add("Test");


        mGeofenceCoordinates.add(MAYNARD_HOUSE);
        //mGeofenceCoordinates.add(VAREY_CURVE);
        mGeofenceCoordinates.add(VILLAGE_BEAUMONT);
        mGeofenceCoordinates.add(SANTANDER);
        //mGeofenceCoordinates.add(new LatLng(51.557935, 0.002382));
        //mGeofenceCoordinates.add(new LatLng(51.526590, -0.039799));
        mGeofenceCoordinates.add(new LatLng(51.526569, -0.039912));
        mGeofenceCoordinates.add(new LatLng(51.526185, -0.039564));
        mGeofenceCoordinates.add(new LatLng(51.557910, 0.002358));
        mGeofenceCoordinates.add(new LatLng(51.554792, 0.001759));


        for(int i=0; i<mGeofenceNames.size(); i++) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(mGeofenceNames.get(i))
                            //(latitude, longitude, radius_in_meters)
                    .setCircularRegion(mGeofenceCoordinates.get(i).latitude, mGeofenceCoordinates.get(i).longitude, 100)
                            //expiration in milliseconds
                    .setExpirationDuration(300000000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }
        //Add geofences to GeofenceStore obect
        mGeofenceStore = new GeofenceStore(this, mGeofenceList); //Send over context and geofence list

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth_angle = event.values[0];
        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];
        // Do something with these orientation angles.

        Log.v("Orientation", Float.toString(event.values[0]));
       // try
       // {
           // Thread.sleep(100);
       // }
        //catch(InterruptedException e){}
    }

}



