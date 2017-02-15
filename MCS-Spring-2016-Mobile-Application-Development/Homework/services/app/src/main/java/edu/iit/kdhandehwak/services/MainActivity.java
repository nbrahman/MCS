package edu.iit.kdhandehwak.services;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CouterService mBoundService;
    boolean mServiceBound = false;
    public EditText timestampText;

    // Initializing service connection
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        // On unexpected disconnection only
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CouterService.MyBinder myBinder = (CouterService.MyBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.isActivityRunning = true;
            mServiceBound = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timestampText = (EditText) findViewById(R.id.editText);

        // Checking for serrvice if already ruuning if yes call startservice to rebind service
        if(isMyServiceRunning(CouterService.class)) {
            Toast.makeText(getBaseContext(), "Rebinding Service", Toast.LENGTH_SHORT).show();
            startService(timestampText);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mServiceBound)
            mBoundService.isActivityRunning = false;
    }


    // Starting service on clicking Start Button
    public void startService(View v) {
        if(!mServiceBound) {
            Toast.makeText(getBaseContext(), "Starting Service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), CouterService.class);
            intent.putExtra("counter", timestampText.getText().toString());
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            Toast.makeText(getBaseContext(), "Already Service running", Toast.LENGTH_SHORT).show();
        }
    }

    // Stoping service on clicking Stop Button
    public void stopService(View v) {
        if(mServiceBound) {
            mServiceBound = false;

            Intent intent = new Intent(this, CouterService.class);

            stopService(intent);
            unbindService(mServiceConnection);
            Toast.makeText(getBaseContext(), "Stopping Service", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getBaseContext(), "No Service Running", Toast.LENGTH_SHORT).show();
        }
    }


    // Check if service is already running in background mode..
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
