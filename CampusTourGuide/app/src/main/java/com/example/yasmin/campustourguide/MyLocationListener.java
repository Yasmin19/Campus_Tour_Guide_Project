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

    @Override
    public void onLocationChanged(Location loc) {

        float acc = loc.getAccuracy();

        loc.getLatitude();
        loc.getLongitude();

        Location dest = new Location("Dest");
        dest.setLatitude(51.525095);
        dest.setLongitude(-0.039004);

        Location home = new Location("home");
        home.setLatitude(51.557929);
        home.setLongitude(0.002362);

        String Text = "My current location is: " +
                "Latitude = " + loc.getLatitude() +
                "Longitude = " + loc.getLongitude();

        float currBearing = loc.getBearing();
        float degree = loc.bearingTo(dest);
        float distance = loc.distanceTo(home);

        Log.d("Bearing", degree + " degrees");
        Log.d("Current loc", Text);
        Log.d("Distance", distance + " metres");
        Log.d("Accuracy", "" + acc);

        MapsActivity.distanceField.setText
                ("Distance: " + distance + " metres.....Accuracy: " + acc + " metres");

        if ((Math.round(distance) %10) == 0){
            
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
