package com.cs442.nbrahman.assignment6;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		Button btnStartService = (Button)findViewById (R.id.btnStartService);
		//define on click listener for "Start Service" button
		btnStartService.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				//start the service explicitly
				explicitStart ();
			}
		});
		Button btnStopService = (Button)findViewById (R.id.btnStopService);
		//define on click listener for "Start Service" button
		btnStopService.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				//stop the service explicitly
				explicitStop ();
			}
		});
	}

	private void explicitStart() {
		// Explicitly start Counter Service
		Intent intent = new Intent(this, CounterService.class);
		long lngInitialValue=0;
		//read the EdiText service
		EditText etInitialValue = (EditText)findViewById (R.id.etNumber);
		//validate the EditText input to check if blank and if numeric
		if (etInitialValue.getText ().toString ().isEmpty ()==false)
		{
			try
			{
				lngInitialValue = Long.parseLong (etInitialValue.getText ().toString ());
			}
			catch (Exception e)
			{
				lngInitialValue = 1;
			}
		}
		else
		{
			lngInitialValue = 1;
		}
		//store the value in intent
		intent.putExtra ("lngInitialValue", lngInitialValue);
		startService (intent);
	}

	private void explicitStop() {
		// Explicitly stop Counter Service
		Intent intent = new Intent(this, CounterService.class);
		stopService (intent);
	}
}
