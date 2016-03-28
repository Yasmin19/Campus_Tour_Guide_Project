package com.example.yasmin.campustourguide;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.PulseInput;
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

	public static double azimuthStuff;
	public static double azimuthRaw;
	private DigitalOutput Trigger;
	private PulseInput Echo;
	private Context mContext=this;
	public static DigitalOutput led_;
	public static DigitalOutput led2;
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
				//led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);

				//Toast.makeText(getApplicationContext(), MapsActivity.msg, Toast.LENGTH_SHORT).show();
				/*Right_Forward = ioio_.openPwmOutput(34, 100);
				Right_Back = ioio_.openPwmOutput(35, 100);
				Left_Forward = ioio_.openPwmOutput(36, 100);
				Left_Back = ioio_.openPwmOutput(37, 100);*/
				Forward[0] = ioio_.openPwmOutput(36, 100);
				Forward[1] = ioio_.openPwmOutput(34, 100);
				Backward[0] = ioio_.openPwmOutput(37, 100);
				Backward[1] = ioio_.openPwmOutput(35, 100);
				//led2 = ioio_.openDigitalOutput(41);

				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
				led2 = ioio_.openDigitalOutput(41);

				for (int i = 0; i < 4; i++) {
					Encoder[i] = ioio_.openDigitalInput(10 + i, DigitalInput.Spec.Mode.PULL_DOWN);//pins 10-13
				}
				Trigger = ioio_.openDigitalOutput(1);
				Echo = ioio_.openPulseInput(2, PulseInput.PulseMode.POSITIVE);

				//Thread sensorThread = new SensorActivity(ioio_);
				//sensorThread.start();


			}
			double thing=0.00087266;


			public int mtodeg(double m)
			{
				m= m+m*0.30;
				return (int)(m/thing);
			}



			public void stopMotors() throws InterruptedException, ConnectionLostException {
				Forward[1].setDutyCycle(0f);
				Forward[0].setDutyCycle(0f);
				Backward[0].setDutyCycle(0f);
				Backward[1].setDutyCycle(0f);
			}

			int flag = 0;
			long sysTime = 0;
			double val;

			@Override
			public void loop() throws ConnectionLostException, InterruptedException {
				MapsActivity.flagButton.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View arg0) {
						Button thing = (Button) arg0;
						flag = 2;
					}
				});
				if (flag == 2) {
					codeCheckCompass((int)MapsActivity.locBearing[0],(double)MapsActivity.locDistance[0]);
					codeCheckCompass((int)MapsActivity.locBearing[1],(double)MapsActivity.locDistance[1]);
					codeCheckCompass((int)MapsActivity.locBearing[2],(double)MapsActivity.locDistance[2]);
					codeCheckCompass((int)MapsActivity.locBearing[3],(double)MapsActivity.locDistance[3]);
					flag++;
				}
				/*if(sense()<40)
				{
					stopMotors();
				}*/
			}
			//codeCheck(1, mtodeg(10));//right wheel needs 2 less drg than left
			//Thread.sleep(3000);

			/*public void codeCheck(int deg) throws ConnectionLostException, InterruptedException {
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
				while ((deg != 0 || deg1!=0)) {//&& !MapsActivity.esc) {
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
						Forward[1].setDutyCycle(0);
					}
					if (deg1 + adapt1 < deg) {
						Forward[1].setDutyCycle(speedR+0.05f);

					}
					if (deg + adapt1 < deg1) {
						Forward[1].setDutyCycle(speedR-0.05f);
					}

				}
			}*/


			/*
					while(flag3==0) {
				azimuthAver = azimuthStuff;
				if(abs(azimuthAver-angle)>10)
				{
					if(((azimuthAver-angle)<0))//
					{
						codeCheckturnRight((int) abs(azimuthAver - angle));
						Thread.sleep(3000);
						//Toast.makeText(getApplicationContext(), Double.toString(azimuthAve), Toast.LENGTH_SHORT).show();
					}
					else
					{
						codeCheckturnLeft((int) abs(azimuthAver-angle));
						Thread.sleep(3000);
						//Toast.makeText(getApplicationContext(),Double.toString(azimuthAve) , Toast.LENGTH_SHORT).show();
					}

				}
				else
				{
					//Toast.makeText(getApplicationContext(), Double.toString(azimuthAve), Toast.LENGTH_SHORT).show();
					//Log.v("Orientation", Double.toString(azimuthAve));
					flag3++;
				}*/







			public double abs(double val) {
				if (val < 0) {
					val = -val;
				}
				return val;
			}

			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			int extra = 5;//extra used for tuning the sensitivity

			public void codeCheckCompass(int angle, double metres) throws ConnectionLostException, InterruptedException {
				if(angle<0)
				{
					angle+=360;
				}
				int deg;
				int deg1;
				float speedL = 0.5f;
				float speedR = 0.5f;
				float thingy = 1f;
				double azimuthAver;
				speedL *= thingy;
				speedR *= thingy;
				flag1 = 0;
				flag2 = 0;
				flag3 = 0;
				while (azimuthStuff == -1) ;

				while (flag1 == 0) {
					azimuthAver = azimuthStuff;
					if (azimuthAver < 10 && 350 < angle) {
						if ((360 - angle + azimuthAver) > 10) {
							stopMotors();
							gotoAngle(angle);
							flag2 = 1;
						}
					}
					if (azimuthAver > 350 - extra && angle < 10 + extra && flag2 == 0) {
						if ((360 - azimuthAver + angle) > 5 + extra) {
							stopMotors();
							gotoAngle(angle);
							flag2 = 1;
						}
					}
					if (azimuthAver > (angle + 10) && flag2 == 0) {
						stopMotors();
						gotoAngle(angle);
						flag2 = 1;
					}
					if (azimuthAver + 10 < angle && flag2 == 0) {
						stopMotors();
						gotoAngle(angle);
						flag2 = 1;
					}
					if (flag2 == 1) {
						Thread.sleep(500);
						if (metres >= 1) {
							deg = mtodeg(1);
							deg1 = deg - 2;
							metres-=1;
						}
						else {
							deg=mtodeg(metres);
							deg1=deg-2;
							metres-=metres;
							flag1=1;
							flag2=1;
							flag3=1;
						}
						A[0] = Encoder[0].read();
						B[0] = Encoder[1].read();
						A[1] = Encoder[2].read();
						B[1] = Encoder[3].read();
						Forward[0].setDutyCycle(speedL);
						Forward[1].setDutyCycle(speedR);
						flag2 = 0;
						flag3=0;
						while (flag3 == 0) {
							if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
								deg--;
								A[0] = temp1;
								B[0] = temp2;
							}
							if ((A[1] != (temp3 = Encoder[2].read()) || B[1] != (temp4 = Encoder[3].read())) && deg1 != 0) {
								deg1--;
								A[1] = temp3;
								B[1] = temp4;
							}
							if (deg1 == 0 || deg == 0) {
								flag3++;
								stopMotors();
							}
						}
					}
				}
			}


/*
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(0f);
				Forward[1].setDutyCycle(speedR);

				flag1=0;
				while(flag1==0){
					azimuthAve = MyOrientationListener.azimuthAve;
					if (azimuthAve <10 && 350 < angle) {
						if((360-angle + azimuthAve)>5){
							stopMotors();
							codeCheckturnLeft((int) (360 - angle + azimuthAve));
							flag2=1;
						}
					}
					if(azimuthAve>350 && angle<10 && flag2==0){
						if((360-azimuthAve + angle)>5) {
							stopMotors();
							codeCheckturnRight((int) (360 - azimuthAve + angle));
							flag2=1;
						}
					}
					if(azimuthAve>(angle+5) && flag2==0)
					{
						stopMotors();
						codeCheckturnLeft((int) (azimuthAve - angle));
						flag2=1;
						Thread.sleep(500);
					}
					if(azimuthAve+5<angle && flag2==0)
					{
						stopMotors();
						codeCheckturnRight((int) (angle - azimuthAve));
						flag2=1;
					}
					if(flag2==1) {
						Thread.sleep(500);
						A[0] = Encoder[0].read();
						B[0] = Encoder[1].read();
						A[1] = Encoder[2].read();
						B[1] = Encoder[3].read();
						Forward[0].setDutyCycle(speedL);
						Forward[1].setDutyCycle(speedR);
						flag2 = 0;
					}
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if ((A[1] != (temp3 = Encoder[2].read()) || B[1] != (temp4 = Encoder[3].read())) && deg1 != 0) {
						deg1--;
						A[1] = temp3;
						B[1] = temp4;
					}
					if (deg1 == 0 || deg == 0) {
						flag1++;
					}
				}
				flag1=0;
				stopMotors();*/
				/*if(MapsActivity.esc)
				{
					Forward[0].setDutyCycle(0);
					Forward[1].setDutyCycle(0);
				}*/
			//Forward[side].setDutyCycle(0);


			public boolean sensorStop()throws InterruptedException,ConnectionLostException
			{
				if(sense()<60)
				{
					return false;
				}
				return true;
			}


			int extradeg=10;
			public void gotoAngle(int angle)throws ConnectionLostException,InterruptedException
			{

				if(azimuthStuff-angle<0)
				{
					Backward[1].setDutyCycle(0.30f);
					Forward[0].setDutyCycle(0.30f);
				}
				else
				{
					Backward[0].setDutyCycle(0.30f);
					Forward[1].setDutyCycle(0.30f);
				}
				while(abs(azimuthStuff-angle)>10);
				stopMotors();


			}


			public void codeCheck(int deg) throws ConnectionLostException, InterruptedException {
				int deg1=deg-2;
				float stuff=0.005f;
				int trigg=0;
				int adapt1=10;
				int adapt2=10;
				float speedL=0.5f;
				float speedR=0.5f;
				float thingy=1f;
				speedL*=thingy;
				speedR*=thingy;
				float speedLorig=speedL;
				float speedRorig=speedR;
				int checkflag=0;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(speedL);
				Forward[1].setDutyCycle(speedR);
				while (checkflag==0){//&& !MapsActivity.esc) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read()))&&deg!=0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if ((A[1] != (temp3 = Encoder[2].read()) || B[1] != (temp4 = Encoder[3].read()))&&deg1!=0) {
						deg1--;
						A[1] = temp3;
						B[1] = temp4;
					}
					if(deg1==0 || deg==0)
					{
						Forward[1].setDutyCycle(0);
						deg=0;
						deg1=0;
						Forward[0].setDutyCycle(0);
						checkflag++;
					}
					if(deg1+adapt1<deg)
					{
						if (trigg!=1 && speedLorig<speedL+0.1*thingy) {
							speedL += stuff*thingy;
							Forward[0].setDutyCycle(speedL);
							trigg = 1;
						}
					}
					else if(deg1+adapt1+10<deg)
					{
						if (trigg!=2 && speedLorig<speedL+0.1*thingy) {
							speedL += stuff*thingy;
							Forward[0].setDutyCycle(speedL);
							trigg = 2;
						}
					}
					else if(trigg==1 || trigg==2)
					{
						speedL -= stuff*trigg*thingy;
						Forward[0].setDutyCycle(speedL);
						trigg=0;
					}
					if(deg+adapt2<deg1)
					{
						if(trigg!=3 && speedRorig<speedR+0.1*thingy)
						{
							speedR+=stuff*thingy;
							Forward[1].setDutyCycle(speedR);
							trigg=3;
						}
					}
					else if(deg+adapt2+10<deg1)
					{
						if (trigg!=4 && speedRorig<speedR+0.1*thingy) {
							speedR += stuff*thingy;
							Forward[1].setDutyCycle(speedR);
							trigg = 4;
						}
					}
					else if(trigg==3 || trigg==4)
					{
						speedR-=stuff*(trigg-2)*thingy;
						Forward[1].setDutyCycle(speedR);
						trigg=0;

					}
				}
				stopMotors();
				/*if(MapsActivity.esc)
				{
					Forward[0].setDutyCycle(0);
					Forward[1].setDutyCycle(0);
				}*/
				//Forward[side].setDutyCycle(0);
			}

			public void codeCheckturnLeft(int angle) throws ConnectionLostException, InterruptedException {
				//int deg1 = deg - 2;
				int deg=(394/90)*angle;
				int deg1=(393/90)*angle;
				flag1=0;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Backward[0].setDutyCycle(0.5f);
				Forward[1].setDutyCycle(0.52f);
				while ((flag1!=2)){//&& !MapsActivity.esc) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if (deg == 0) {
						Backward[0].setDutyCycle(0f);
						flag1++;
					}
					if ((A[1] != (temp1 = Encoder[2].read()) || B[1] != (temp2 = Encoder[3].read())) && deg1 != 0) {
						deg1--;
						A[1] = temp1;
						B[1] = temp2;
					}
					if (deg1 == 0) {
						Forward[1].setDutyCycle(0f);
						flag1++;
					}
				}
				flag1=0;
				stopMotors();
				/*if(MapsActivity.esc)
				{
					Backward[0].setDutyCycle(0);
					Forward[1].setDutyCycle(0);
				}*/
				//Forward[side].setDutyCycle(0);
			}

			public void codeCheckturnRight(int angle) throws ConnectionLostException, InterruptedException {
				//int deg1 = deg - 2;
				int deg=(393/90)*angle;
				int deg1=(394/90)*angle;
				int rightflag=0;
				flag1=0;
				A[0] = Encoder[0].read();
				B[0] = Encoder[1].read();
				A[1] = Encoder[2].read();
				B[1] = Encoder[3].read();
				Forward[0].setDutyCycle(0.5f);
				Backward[1].setDutyCycle(0.52f);
				while ((rightflag!=2)){//&& !MapsActivity.esc) {
					if ((A[0] != (temp1 = Encoder[0].read()) || B[0] != (temp2 = Encoder[1].read())) && deg != 0) {
						deg--;
						A[0] = temp1;
						B[0] = temp2;
					}
					if (deg == 0) {
						Forward[0].setDutyCycle(0);
						rightflag++;
					}
					if ((A[1] != (temp1 = Encoder[2].read()) || B[1] != (temp2 = Encoder[3].read())) && deg1 != 0) {
						deg1--;
						A[1] = temp1;
						B[1] = temp2;
					}
					if (deg1 == 0) {
						Backward[1].setDutyCycle(0);
						rightflag++;
					}
				}
				rightflag=0;
				stopMotors();
				/*if(MapsActivity.esc)
				{
					Backward[1].setDutyCycle(0);
					Forward[0].setDutyCycle(0);
				}*/
				//Forward[side].setDutyCycle(0);
			}
			//394 right
			//393 left for 90 degree turn



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


	static int marioflag=0;
	public static boolean asd=false;
	public static boolean asdf=false;
	public static void marioStuff(double az,double azraw) throws ConnectionLostException {
		azimuthStuff=az;
		azimuthRaw=azraw;
		asd=!asd;
		led_.write(asd);
		marioflag++;
		if(marioflag>50){
			marioflag=0;
			asdf=!asdf;
			led2.write(asdf);
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}}