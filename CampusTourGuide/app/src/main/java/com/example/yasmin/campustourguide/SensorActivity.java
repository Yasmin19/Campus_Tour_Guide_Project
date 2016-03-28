package com.example.yasmin.campustourguide;

import android.content.Context;
import android.widget.Toast;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
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

    public SensorActivity(IOIO ioio_) throws ConnectionLostException{
        this.ioio = ioio_;
        //this.led = led_;
       // mContext =con;

        //Toast.makeText(mContext, "In IOIO sub thread", Toast.LENGTH_SHORT).show();

        //led = ioio.openDigitalOutput(0,true);
       led = ioio_.openDigitalOutput(IOIO.LED_PIN);
    }

    public void run(){
       // Toast.makeText(mContext, "In IOIO sub thread - run method", Toast.LENGTH_SHORT).show();
        try {
             while(true) {
                led.write(true);
                Thread.sleep(500);
                 led.write(false);
                 Thread.sleep(500);
             }
        } catch (ConnectionLostException e) {
          } catch (InterruptedException e){}
    }

    protected void onStop() {
        //mGeofenceStore.disconnect();
        led.close();
    }

}



