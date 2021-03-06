package com.example.yasmin.campustourguide;

import android.content.Context;
import android.widget.Toast;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;

/**
 * Created by Yasmin on 28/03/2016.
 */
public class SensorActivity extends Thread{
    private IOIO ioio;
    private DigitalOutput led;
    private Context mContext;
    private DigitalOutput Trigger;
    private PulseInput Echo;
    public static double lastVal = 0.0;

    public SensorActivity(IOIO ioio_) throws ConnectionLostException{
        this.ioio = ioio_;
        //this.led = led_;
       // mContext =con;

        //Toast.makeText(mContext, "In IOIO sub thread", Toast.LENGTH_SHORT).show();

        //led = ioio.openDigitalOutput(0,true);
        led = ioio_.openDigitalOutput(IOIO.LED_PIN);
        Trigger = ioio_.openDigitalOutput(1);
        Echo = ioio_.openPulseInput(2, PulseInput.PulseMode.POSITIVE);

       // Toast.makeText(mContext, "In IOIO Sensor Activity", Toast.LENGTH_SHORT).show();
    }

    public void run(){
       // Toast.makeText(mContext, "In IOIO sub thread - run method", Toast.LENGTH_SHORT).show();
        try {
             while(true) {

                // lastVal = 20.0;
                 lastVal=sense();
                 //Thread.sleep(100);
             }
        } catch (ConnectionLostException e) {
          } catch (InterruptedException e){}
    }

    protected void onStop() {
        //mGeofenceStore.disconnect();
        led.close();
    }

    public double sense()throws ConnectionLostException,InterruptedException
    {
        ping();
        return detect();
    }


    public void ping()throws ConnectionLostException, InterruptedException {
        Trigger.write(false);
        Thread.sleep(5);
        Trigger.write(true);
        Thread.sleep(1);
        Trigger.write(false);
    }



    public double detect()throws InterruptedException, ConnectionLostException
    {
        return Echo.getDuration() * 34000 / 2;
    }

}



