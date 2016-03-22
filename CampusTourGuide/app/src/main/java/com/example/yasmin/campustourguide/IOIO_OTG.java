package com.example.yasmin.campustourguide;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import ioio.lib.util.android.IOIOService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 *
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */

public class IOIO_OTG extends IOIOService {

	private PwmOutput Right_Forward;
	private PwmOutput Right_Back;
	private PwmOutput Left_Forward;
	private PwmOutput Left_Back;
	private int angle;
	private float right;
	private float left;
	int delay;

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			private DigitalOutput led_;

			@Override
			protected void setup() throws ConnectionLostException, InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);

				Right_Forward = ioio_.openPwmOutput(12, 100);
				Right_Back = ioio_.openPwmOutput(13, 100);
				Left_Forward = ioio_.openPwmOutput(14, 100);
				Left_Back = ioio_.openPwmOutput(11, 100);

				switch(angle){
					case 0:
						right = 0.57f;
						left = 0.55f;
						delay = 0;
						break;
					case 90:
						right = 1f;
						left = 1f;
						delay = 0;

					case 270:
						right = 1f;
						left = 1f;
						delay = 825;
				}

			}

			@Override
			public void loop() throws ConnectionLostException, InterruptedException {

				led_.write(true);
				Thread.sleep(500);
			}
		};
	}

	public IBinder onBind(Intent arg0){
		return null;
	}
}