package com.example.yasmin.campustourguide;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Yasmin on 16/03/2016.
 */
public class MyOrientationListener extends Activity implements SensorEventListener{

    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("Orientation", "Reached here");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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
    }

}
