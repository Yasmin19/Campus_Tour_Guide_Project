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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * This is the main activity of the HelloIOIO example application.
 *
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */

public class IOIO_OTG extends IOIOService {

	ArrayList<Geofence> mGeofenceList; //List of geofences used
	ArrayList<String> mGeofenceNames; //List of geofence names
	ArrayList<LatLng> mGeofenceCoordinates; //List of geofence coordinates
	public GeofenceStore mGeofenceStore;
	private static final LatLng MAYNARD_HOUSE = new LatLng(51.525095, -0.039004);
	//private static final LatLng VAREY_CURVE = new LatLng(51.525355, -0.039331);
	private static final LatLng VILLAGE_BEAUMONT = new LatLng(51.525579, -0.039499);
	private static final LatLng SANTANDER = new LatLng(51.526144, -0.039733);

	private Context mContext=this;
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
				//populateGeofences();
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
				m= m+m*0.30;
				return (int)(m/thing);
			}

			int flag=0;
			@Override
			public void loop() throws ConnectionLostException, InterruptedException {
				MapsActivity.flagButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View arg0) {
						Button thing = (Button) arg0;
						try {
							//codeCheck(mtodeg(3));
							//codeCheckturnLeft(MapsActivity.angleLeft, MapsActivity.angleRight);//Right 393  Left 394
							//codeCheckturnRight(393, 394);//right 394  left 393
							codeCheck(mtodeg(50));
							/*
							if(MapsActivity.esc)
							{
								MapsActivity.esc=false;
								codeCheckturnRight(393, 394);
							}*/
							//codeCheck(1, mtodeg(3));
						} catch (ConnectionLostException e) {
						} catch (InterruptedException b) {
						}
					}
				});
				//if (MapsActivity.esc) {
			}

			//codeCheck(1, mtodeg(10));//right wheel needs 2 less drg than left
			//Thread.sleep(3000);


			public void codeCheck(int deg) throws ConnectionLostException, InterruptedException {
				int deg1=deg-2;
				float stuff=0.005f;
				int trigg=0;
				int adapt1=10;
				int adapt2=10;
				float speedLorig=0.5f;
				float speedRorig=0.52f;
				float speedL=0.5f;
				float speedR=0.525f;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(0.5f);
				Forward[1].setDutyCycle(0.52f);
				while ((deg != 0 || deg1!=0)&& !MapsActivity.esc) {
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
					if(deg1+adapt1<deg)
					{
						if (trigg!=1 && speedLorig<speedL+0.1) {
							speedL += stuff;
							Forward[0].setDutyCycle(speedL);
							trigg = 1;
						}
					}
					else if(deg1+adapt1+10<deg)
					{
						if (trigg!=2 && speedLorig<speedL+0.1) {
							speedL += stuff;
							Forward[0].setDutyCycle(speedL);
							trigg = 2;
						}
					}
					else if(trigg==1 || trigg==2)
					{
						speedL -= stuff*trigg;
						Forward[0].setDutyCycle(speedL);
						trigg=0;
					}
					if(deg+adapt2<deg1)
					{
						if(trigg!=3 && speedRorig<speedR+0.1)
						{
							speedR+=stuff;
							Forward[1].setDutyCycle(speedR);
							trigg=3;
						}
					}
					else if(deg+adapt2+10<deg1)
					{
						if (trigg!=4 && speedRorig<speedR+0.1) {
							speedR += stuff;
							Forward[1].setDutyCycle(speedR);
							trigg = 4;
						}
					}
					else if(trigg==3 || trigg==4)
					{
						speedR-=stuff*(trigg-2);
						Forward[1].setDutyCycle(speedR);
						trigg=0;
					}
				}

				if(MapsActivity.esc)
				{
					Forward[0].setDutyCycle(0);
					Forward[1].setDutyCycle(0);
				}
				//Forward[side].setDutyCycle(0);
			}


			public void codeCheckturnLeft(int deg, int deg1) throws ConnectionLostException, InterruptedException {
				//int deg1 = deg - 2;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Backward[0].setDutyCycle(0.5f);
				Forward[1].setDutyCycle(0.52f);
				while ((deg != 0 || deg1 != 0) && !MapsActivity.esc) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if (deg == 0) {
						Backward[0].setDutyCycle(0);
					}
					if ((A[1] != (temp1 = Encoder[2].read()) || B[1] != (temp2 = Encoder[3].read())) && deg1 != 0) {
						deg1--;
						A[1] = temp1;
						B[1] = temp2;
					}
					if (deg1 == 0) {
						Forward[1].setDutyCycle(0);
					}
				}

				if(MapsActivity.esc)
				{
					Backward[0].setDutyCycle(0);
					Forward[1].setDutyCycle(0);
				}
				//Forward[side].setDutyCycle(0);
			}


			public void codeCheckturnRight(int deg, int deg1) throws ConnectionLostException, InterruptedException {
				//int deg1 = deg - 2;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(0.5f);
				Backward[1].setDutyCycle(0.52f);
				while ((deg != 0 || deg1 != 0) && !MapsActivity.esc) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if (deg == 0) {
						Forward[0].setDutyCycle(0);
					}
					if ((A[1] != (temp1 = Encoder[2].read()) || B[1] != (temp2 = Encoder[3].read())) && deg1 != 0) {
						deg1--;
						A[1] = temp1;
						B[1] = temp2;
					}
					if (deg1 == 0) {
						Backward[1].setDutyCycle(0);
					}
				}

				if(MapsActivity.esc)
				{
					Backward[1].setDutyCycle(0);
					Forward[0].setDutyCycle(0);
				}
				//Forward[side].setDutyCycle(0);
			}
			//394 right
			//393 left for 90 degree turn


			public void populateGeofences() {

				//Empty list for storing geofences
				mGeofenceNames = new ArrayList<String>();
				mGeofenceCoordinates = new ArrayList <LatLng>();
				mGeofenceList = new ArrayList<Geofence>();

				mGeofenceNames.add("Maynard House");
				//mGeofenceNames.add("Varey House/The Curve");
				mGeofenceNames.add("Village Shop/Beaumont Court");
				mGeofenceNames.add("Santander Bank");
				//mGeofenceNames.add("Home");
				//mGeofenceNames.add("France House");
				mGeofenceNames.add("Turning Point");
				mGeofenceNames.add("Canalside");


				mGeofenceCoordinates.add(MAYNARD_HOUSE);
				//mGeofenceCoordinates.add(VAREY_CURVE);
				mGeofenceCoordinates.add(VILLAGE_BEAUMONT);
				mGeofenceCoordinates.add(SANTANDER);
				//mGeofenceCoordinates.add(new LatLng(51.557935, 0.002382));
				//mGeofenceCoordinates.add(new LatLng(51.526590, -0.039799));
				mGeofenceCoordinates.add(new LatLng(51.526569, -0.039912));
				mGeofenceCoordinates.add(new LatLng(51.526185, -0.039564));


				for(int i=0; i<mGeofenceNames.size(); i++) {
					mGeofenceList.add(new Geofence.Builder()
							// Set the request ID of the geofence. This is a string to identify this
							// geofence.
							.setRequestId(mGeofenceNames.get(i))
									//(latitude, longitude, radius_in_meters)
							.setCircularRegion(mGeofenceCoordinates.get(i).latitude, mGeofenceCoordinates.get(i).longitude, 20)
									//expiration in milliseconds
							.setExpirationDuration(300000000)
							.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
							.build());
				}
				//Add geofences to GeofenceStore obect
				mGeofenceStore = new GeofenceStore(mContext, mGeofenceList); //Send over context and geofence list

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

	public void onDestroy(){
		stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}