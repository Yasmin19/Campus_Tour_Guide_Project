package com.example.yasmin.campustourguide;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yasmin on 08/02/2016.
 */
public class MyLocationListener implements LocationListener {
    private Context mContext;
    private boolean flag = false;

    @Override
    public void onLocationChanged(Location loc) {

        float acc = loc.getAccuracy();

        loc.getLatitude();
        loc.getLongitude();

        //Santander Bank
        Location dest = new Location("Dest");
        dest.setLatitude(51.526144);
        dest.setLongitude(-0.039733);

        String Text = "My current location is: " +
                "Latitude = " + loc.getLatitude() +
                "Longitude = " + loc.getLongitude();

        float currBearing = loc.getBearing();
        //float degree = loc.bearingTo(srt);
        float distance = loc.distanceTo(dest);
    /*
        Log.d("Bearing", degree + " degrees");
        Log.d("Current loc", Text);
        Log.d("Distance", distance + " metres");
        Log.d("Accuracy", "" + acc);*/

       // int[] angle = {0,2,5,8,5,4,0,0};

        //MapsActivity.distanceField.setText
                //("Distance: " + distance + " metres.....Accuracy: " + acc + " metres");


        double nearest_10 = 10*(Math.floor(distance/10));
        Log.d("Rounded", "" + nearest_10);

        /*
        if (((nearest_10 % 10) == 0) && (nearest_10 <= 70)){

            String text = "From : " + nearest_10 + "m travel at " + angle[(int)nearest_10/10] + " degrees ..... Actual Distance: " + distance;
            //MapsActivity.distanceField.setText(text);
            
        }*/

        //for testing, manually make distance a certain amount, create a delay and then change to less than 20 to test whether stops or not
        if (!flag) {
            if (distance >= 100) {
                MapsActivity.esc = true;
                flag = true;
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
