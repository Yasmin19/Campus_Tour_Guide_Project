package com.example.yasmin.campus_tour_guide;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;

import android.util.Log;



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

        loc.getLatitude();
        loc.getLongitude();

        Location home = new Location("home");
        home.setLatitude(51.557929);
        home.setLongitude(0.002362);

        Location dest = new Location("Dest");
        dest.setLatitude(51.525095);
        dest.setLongitude(-0.039004);

        String Text = "My current location is: " +
        "Latitude = " + loc.getLatitude() +
        "Longitude = " + loc.getLongitude();


       // Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();

        float currBearing = loc.getBearing();

        float degree = loc.bearingTo(dest);

        float distance = loc.distanceTo(home);
        Log.d("Bearing", degree + " degrees");
        Log.d("Current loc", Text);
        Log.d("Distance", distance + " metres");
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
