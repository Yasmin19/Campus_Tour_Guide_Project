package com.example.yasmin.campus_tour_guide;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Yasmin on 08/02/2016.
 */
public class MyLocationListener implements LocationListener {
    private Context mContext;
    private Context activity;

    @Override
    public void onLocationChanged(Location loc) {

        loc.getLatitude();
        loc.getLongitude();

        Location dest = new Location("Dest");
        dest.setLatitude(51.558101);
        dest.setLongitude(0.002598);

        String Text = "My current location is: " +
        "Latitude = " + loc.getLatitude() +
        "Longitude = " + loc.getLongitude();


       // Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();

        float currBearing = loc.getBearing();
        float degree = loc.bearingTo(dest);
        //Context context = getApplicationContext();

        Toast.makeText(mContext, String.valueOf(degree), Toast.LENGTH_SHORT).show();

        //AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //builder.setMessage(degree + " degrees")
        //        .setTitle("Bearing");

        //AlertDialog dialog = builder.create();

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
