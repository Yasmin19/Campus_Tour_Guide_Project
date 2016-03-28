package com.example.yasmin.campustourguide;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Yasmin on 16/03/2016.
 */
public class MyOrientationListener extends Service implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    float mGravity[];
    float mGeomagnetic[];
    float orientation[] = new float[3];
    float Rot[] = new float[9];
    float I[] = new float[9];
    boolean success;
    float azimuth;
    float pitch_angle;
    float roll_angle;

    public static double azimuthLPF=-1;
    int alpha=10;
    public static double azimuthDeg;
    public static double azimuth360;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);


        return START_STICKY;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {
            success = SensorManager.getRotationMatrix(Rot, I, mGravity, mGeomagnetic);
            if (success) {
                SensorManager.getOrientation(Rot, orientation);
                azimuth = orientation[0];
                pitch_angle = orientation[1];
                roll_angle = orientation[2];

                azimuthDeg = Math.toDegrees(azimuth);

                if (azimuthDeg < 0) {
                    azimuth360 = azimuthDeg + 360;
                }
                // Log.v("Orientation", Double.toString(Math.toDegrees(azimuth360)));
                Log.v("Orientation", Double.toString(azimuthDeg));
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
