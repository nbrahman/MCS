package edu.iit.kdhandehwak.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class CouterService extends Service {
    private IBinder mBinder = new MyBinder();
    Boolean isActivityRunning;

    int counter;
    Timer timer = new Timer();

    @Override
    public void onCreate() {
        Log.i("LocalService", "Service created");
        super.onCreate();
        counter = -1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // If app gets killed
        if(intent==null)
            counter = SP.getInt("counter", counter);

        else {
            // If service launched from activity
            if (counter == -1) {
                try {
                    String counterStr = intent.getStringExtra("counter");
                    counter = Integer.parseInt(counterStr);
                } catch (Exception e) {
                    counter = 0;
                }
            }
        }
        // Timer code
        TimerTask updateTime = new UpdateTimeTask();
        timer.scheduleAtFixedRate(updateTime, 0, 1000);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i("LocalService", "Service onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i("LocalService", "Service onDestroy");
        super.onDestroy();
        timer.cancel();
    }

    public class MyBinder extends Binder {
        // getting service instance...
        CouterService getService() {
            return CouterService.this;
        }
    }

    class UpdateTimeTask extends TimerTask {
        public void run() {
            changeCounterText();
        }

        private void changeCounterText() {
            if(counter > 0) {
                // SHow notifications if counter is equal to 10
                if (counter % 10 == 0)
                    showNotification(counter);

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = SP.edit();
                edit.putInt("counter", counter);
                edit.apply();

                Log.i("LocalService", "Count : " + counter);

            }
            counter++;
        }

        private void showNotification(int text) {
            Log.i("LocalService", "Notification sent");
            NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Timer Alert")
                    .setContentText(String.valueOf(text + " sec"));
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mBuilder.setVibrate(new long[]{1000, 1000});
            mBuilder.setLights(Color.RED, 3000, 3000);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1, mBuilder.build());
        }
    }
}
