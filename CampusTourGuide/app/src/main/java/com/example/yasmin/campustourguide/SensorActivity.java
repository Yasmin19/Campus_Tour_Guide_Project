package com.example.yasmin.campustourguide;

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

    public SensorActivity(IOIO ioio_, DigitalOutput led_) throws ConnectionLostException{
        this.ioio = ioio_;
        this.led = led_;

        //led = ioio.openDigitalOutput(0,true);
       // led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);

    }

    public void run(){
        try {
             while(true) {
                led.write(true);
                sleep(500);
                 led.write(false);
                 sleep(500);

                }
        } catch (ConnectionLostException e) {
          } catch (InterruptedException e){}
    }

}



