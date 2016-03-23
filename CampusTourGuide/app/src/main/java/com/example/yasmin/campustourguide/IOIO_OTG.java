package com.example.yasmin.campustourguide;

import ioio.lib.api.DigitalInput;
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

import java.net.URISyntaxException;

/**
 * This is the main activity of the HelloIOIO example application.
 *
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */

public class IOIO_OTG extends IOIOService {

	private DigitalOutput led_;
	private  PwmOutput Forward[] = new PwmOutput[2];
	private  PwmOutput Backward[] = new PwmOutput[2];
	private  DigitalInput Encoder[] = new DigitalInput[4];
	private int angle;
	private float right;
	private float left;
	private boolean A[]=new boolean[2];//tracks
	private boolean B[]=new boolean[2];
	private boolean temp1, temp2,temp3,temp4;
	int delay;

	@Override
	protected IOIOLooper createIOIOLooper()  {
		return new BaseIOIOLooper()  {


			@Override
			protected void setup() throws ConnectionLostException, InterruptedException{
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);

				//Toast.makeText(getApplicationContext(), MapsActivity.msg, Toast.LENGTH_SHORT).show();
				/*Right_Forward = ioio_.openPwmOutput(34, 100);
				Right_Back = ioio_.openPwmOutput(35, 100);
				Left_Forward = ioio_.openPwmOutput(36, 100);
				Left_Back = ioio_.openPwmOutput(37, 100);*/
				Forward[0] = ioio_.openPwmOutput(36, 100);
				Forward[1] = ioio_.openPwmOutput(34, 100);
				Backward[0] = ioio_.openPwmOutput(37, 100);
				Backward[1] = ioio_.openPwmOutput(35, 100);
				int base = 10;
				for (int i = 0; i < 4; i++) {
					Encoder[i] = ioio_.openDigitalInput(10 + i, DigitalInput.Spec.Mode.PULL_DOWN);
				}
				//Right_Forward.setDutyCycle(0.52f);
				//Left_Forward.setDutyCycle(0.5f);
//1+3/4 1 wheel moving for 90 degree turn
				/*
				switch(angle){
					case 0:
						right = 0.57f;
						left = 0.55f;
						delay = 0;
						break;
					case 90:
						right = 58/255f;
						left = 58/255f;
						delay = 0;

					case 270:
						right = 1f;
						left = 1f;
						delay = 825;
				}*/

			}
			double thing=0.00087266;


			public int mtodeg(double m)
			{
				m= m+m*0.25;
				return (int)(m/thing);
			}

			int flag=0;
			@Override
			public void loop() throws ConnectionLostException, InterruptedException {

				//if (MapsActivity.esc) {
				if(flag==0)
				{
					//codeCheck(1, mtodeg(10));//right wheel needs 2 less drg than left
					//Thread.sleep(3000);
					codeCheck(1, mtodeg(3));
					flag++;
				}
			}
			int deg1;
			int deg2;
			public void codeCheck(int side, int deg) throws ConnectionLostException, InterruptedException {
				int deg1=deg-2;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(0.5f);
				Forward[1].setDutyCycle(0.52f);
				while (deg != 0 || deg1!=0) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read()))&&deg!=0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if(deg==0)
					{
						Forward[0].setDutyCycle(0);
					}
					if ((A[1] != (temp1 = Encoder[2].read()) || B[1] != (temp2 = Encoder[3].read()))&&deg1!=0) {
						deg1--;
						A[1] = temp1;
						B[1] = temp2;
					}
					if(deg1==0)
					{
						Forward[1].setDutyCycle(0);
					}
				}
				//Forward[side].setDutyCycle(0);
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		createIOIOLooper();
		int result = super.onStartCommand(intent, flags, startId);

        /*
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals("stop")) {
            // User clicked the notification. Need to stop the service.
            nm.cancel(0);
            stopSelf();
        } else {
            // Service starting. Create a notification.
            Notification notification = new Notification(
                    R.drawable.queenmary, "IOIO service running",
                    System.currentTimeMillis());
            notification
                    .setLatestEventInfo(this, "IOIO Service", "Click to stop",
                            PendingIntent.getService(this, 0, new Intent(
                                    "stop", null, this, this.getClass()), 0));
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            nm.notify(0, notification);
        }*/
		return result;
	}


	protected void onStop() {
		//mGeofenceStore.disconnect();
		led_.close();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}