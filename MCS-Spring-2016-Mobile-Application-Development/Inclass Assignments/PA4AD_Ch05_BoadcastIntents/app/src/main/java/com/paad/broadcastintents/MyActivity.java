package com.paad.broadcastintents;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends Activity {
  /**
   * Listing 5-12: Registering and unregistering a Broadcast Receiver in code 
   */
  private IntentFilter filter = 
      new IntentFilter(LifeformDetectedReceiver.NEW_LIFEFORM);

  private LifeformDetectedReceiver receiver = 
    new LifeformDetectedReceiver();

  @Override
  public synchronized void onResume() {
    super.onResume();

    // Register the broadcast receiver.
    registerReceiver(receiver, filter); 
  }

  @Override
  public synchronized void onPause() {
    // Unregister the receiver
    unregisterReceiver(receiver);  

    super.onPause();
  }
  
  //
  private void detectedLifeform(String detectedLifeform, double currentLongitude, double currentLatitude) {
    Intent intent = new Intent(LifeformDetectedReceiver.NEW_LIFEFORM);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LIFEFORM_NAME,
                    detectedLifeform);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LONGITUDE,
                    currentLongitude);
    intent.putExtra(LifeformDetectedReceiver.EXTRA_LATITUDE,
                    currentLatitude);

    sendBroadcast(intent);
  }
  
  //
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate (savedInstanceState);
      setContentView (R.layout.main);
      Button button2 = (Button)findViewById (R.id.button2);
      button2.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick (View v) {
              TextView tv = (TextView) findViewById (R.id.tv);
              tv.setText ("Call me on 312-402-8757\nYou can also reach me on Facebook.com.\nClick a location like 1600 Amphitheatre Parkway, Mountain View, CA 94043");

          }
      });

      Button button = (Button)findViewById (R.id.button);
      button.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick (View v) {
              receiver.onReceive (getBaseContext (), new Intent (getBaseContext (), MyActivity.class));

          }
      });
  }

}