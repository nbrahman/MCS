package com.cs442.nbrahman.assignment6;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

//Counter Service class
public class CounterService extends Service {
	private NotificationManager nm;
	private Timer timerIncrement = new Timer ();
	private long lngInitialValue = 1;
	private static boolean isRunning = false;
	private int intCounter = 0;
	Context context;

	//Local Binder code
	public class LocalBinder extends Binder {
		CounterService getService () {
			return CounterService.this;
		}
	}

	//onBind event
	@Override
	public IBinder onBind (Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder ();

	@Override
	//OnCreate event to create the service and initialize the required paramenters
	public void onCreate () {
		super.onCreate ();
		Log.i ("Counter Service", "Counter Service created successfully");
	}

	//function to increment counter after every one second and to display notifications after every 10 seconds
	private void IncrementCounter () {
		//Exceute the code only if service is running
		if (isRunning ()) {
			try {
				//increment step variable
				long incrementby = 1;

				//increment the service counter variable
				lngInitialValue += incrementby;
				Log.i ("Counter Service", "Service Counter variable incremented by " + String.valueOf (incrementby) + " successfully");

				//increment the notification counter variable
				intCounter++;

				//store the value of current counter and timer variables in SharedPreferences object
				SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor edit = SP.edit();
				edit.putLong ("lngInitialValue", lngInitialValue);
				edit.putInt ("intCounter", intCounter);
				edit.apply();

				//check if notification counter is in multiple of 10 and display the notification accordingly
				if (intCounter % 10 == 0) {
					showNotification ();
					//reset the notification counter
					intCounter = 0;
					Log.i ("Counter Service", "Notification counter variable incremented successfully");
				}
			} catch (Throwable t) {
				Log.e ("Exception", t.getMessage ());
			}
		}
	}

	//function to initialize and display the noification
	private void showNotification () {
		//get access to Notification Manager service
		nm = (NotificationManager) getSystemService (NOTIFICATION_SERVICE);

		Notification.Builder nBuilder = new Notification.Builder (CounterService.this);

		//initialize the icon
		nBuilder.setSmallIcon (android.R.mipmap.sym_def_app_icon)
				//set the content (Service Counter varable value)
				.setContentText (String.valueOf (this.lngInitialValue))
				.setWhen (System.currentTimeMillis ())
				//set vibration and sound flags
				.setDefaults (Notification.DEFAULT_SOUND |
						Notification.DEFAULT_VIBRATE)
				//set notification ringtone
				.setSound (
						RingtoneManager.getDefaultUri (
								RingtoneManager.TYPE_NOTIFICATION))
				//set vibration pattern
				.setVibrate (new long[]{10000, 100000, 1000000, 10000000, 100000000})
				//set LED notification details
				.setLights (Color.DKGRAY, 0, 1)
				// set Notification title
				.setContentTitle (getText (R.string.service_name));
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity (this, 0, new Intent (this, MainActivity.class), 0);
		//display the notification
		nm.notify (R.string.service_label, nBuilder.getNotification ());
		Log.i ("Counter Service", "Notification displayed successfully");
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		//get access to base context
		context = getBaseContext ();
		//check if service is running
		if (!isRunning ()) {
			SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences (getApplicationContext ());

			// If app gets killed
			if(intent==null) {
				this.lngInitialValue = SP.getLong ("lngInitialValue", lngInitialValue);
				this.intCounter =  SP.getInt ("intCounter", intCounter);
			}
			else {
				//read the intent variables and assign them to service variables
				this.lngInitialValue = intent.getLongExtra ("lngInitialValue", 1);
				intCounter = 0;
				Log.i ("Counter Service", "Counter Service started successfully");
			}
			//set service running flag
			isRunning = true;
			//define timer to increment service counter variable value every second
			timerIncrement.scheduleAtFixedRate (new TimerTask () {
				public void run () {
					IncrementCounter ();
				}
			}, 0, 1000L);
			//display toast message to confirm the service is started with appropriate value from text box
			Toast.makeText (context, "Counter Service started with initial value as " + lngInitialValue, Toast.LENGTH_LONG).show ();
		}
		return START_STICKY; // run until explicitly stopped.
	}

	//function to return service status
	public static boolean isRunning () {
		return isRunning;
	}

	//overriding the onDesttroy event to clear out all the variables
	@Override
	public void onDestroy () {
		super.onDestroy ();
		if (timerIncrement != null) {
			timerIncrement.cancel ();
		}
		//display toast message to confirm the service is stopped
		Toast.makeText (context, "Counter Service stopped with final value as " + lngInitialValue, Toast.LENGTH_LONG).show ();
		//cleanup operation
		this.lngInitialValue = 0;
		this.intCounter = 0;
		//cancel all the notifications
		nm.cancel (R.string.service_label); // Cancel the persistent notification.
		isRunning = false;
		Log.i ("Counter Service", "Counter Service stopped successfully");
	}

/*	@Override
	public void onTaskRemoved (Intent intentRestart) {
		Intent restartServiceIntent = new Intent (getApplicationContext (), this.getClass ());
		restartServiceIntent.setPackage (getPackageName ());
		Log.i ("Counter Service", "Counter Service restarted successfully");
		intentRestart.putExtra ("lngInitialValue", lngInitialValue);

		PendingIntent restartServicePendingIntent =
				PendingIntent.getService (getApplicationContext (), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext ().getSystemService (Context.ALARM_SERVICE);
		alarmService.set (
				AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime () + 1000,
				restartServicePendingIntent);

		super.onTaskRemoved (intentRestart);
	}*/

}