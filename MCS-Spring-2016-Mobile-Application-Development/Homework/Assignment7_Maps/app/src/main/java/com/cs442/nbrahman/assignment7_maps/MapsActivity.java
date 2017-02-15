package com.cs442.nbrahman.assignment7_maps;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	LatLng latlngCurLocation;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
				.findFragmentById (R.id.map);
		mapFragment.getMapAsync (this);

		final EditText etAddress = (EditText)findViewById (R.id.etAddress);
		if (etAddress!=null) {
			etAddress.setOnKeyListener (new View.OnKeyListener () {
				@Override
				public boolean onKey (View v, int keyCode, KeyEvent event) {
					if ((event.getAction () == KeyEvent.ACTION_DOWN) &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						if ((!(etAddress.getText ().toString ().isEmpty ())) && (!(etAddress.getText ().toString ().equals ("")))) {
							findLatLng (etAddress.getText ().toString ());
						} else {
							Toast.makeText (getApplicationContext (), "Please enter adress", Toast.LENGTH_LONG).show ();
							etAddress.requestFocus ();
						}
						return true;
					}
					return false;
				}
			});
		}
		Button btnSubmit = (Button) findViewById (R.id.btnSubmit);
		btnSubmit.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				if (etAddress!=null) {
					if ((!(etAddress.getText ().toString ().isEmpty ())) && (!(etAddress.getText ().toString ().equals ("")))) {
						findLatLng (etAddress.getText ().toString ());
					}
					else
					{
						Toast.makeText (getApplicationContext (), "Please enter adress", Toast.LENGTH_LONG).show ();
						etAddress.requestFocus ();
					}
				}
			}
		});
	}

	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show ();
			return false;
		}
		return true;
	}

	private void findLatLng (String strAddress)
	{
		if (!checkReady()) {
			return;
		}
		Geocoder coder = new Geocoder(this);
		List<Address> address;

		try {
			address = coder.getFromLocationName(strAddress, 5);
			if (address==null) {
				Toast.makeText (getApplicationContext (), "Unable to find Latitude and Longitude based on the address entered!!", Toast.LENGTH_LONG).show ();
			}
			Address location=address.get(0);
			TextView tvLatitude = (TextView) findViewById (R.id.tvLatitude);
			if (tvLatitude!=null)
			{
				tvLatitude.setText (String.valueOf (location.getLatitude ()));
			}

			TextView tvLongitude = (TextView) findViewById (R.id.tvLongitude);
			if (tvLongitude!=null)
			{
				tvLongitude.setText (String.valueOf (location.getLongitude ()));
			}

			if ((tvLatitude!=null) && (tvLongitude!=null)) {
				latlngCurLocation = new LatLng (location.getLatitude (), location.getLongitude ());

				// Add a marker in newly added location and move the camera
				mMap.addMarker (new MarkerOptions ().position (latlngCurLocation).title ("Marker in " + strAddress));
				/*mMap.moveCamera (CameraUpdateFactory.newCameraPosition (new CameraPosition.Builder ().target (latlngCurLocation)
						.zoom (15.5f)
						.bearing (300)
						.tilt (50)
						.build ()));*/
				mMap.animateCamera (CameraUpdateFactory.newCameraPosition (new CameraPosition.Builder ().target (latlngCurLocation)
						.zoom (15.5f)
						.bearing (300)
						.tilt (50)
						.build ()));
				//mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (latlngCurLocation, 12.0f));
			}
		}
		catch (Exception e)
		{
			Toast.makeText (getApplicationContext (), "Unable to find Latitude and Longitude based on the address entered!!", Toast.LENGTH_LONG).show ();
			e.printStackTrace ();
		}
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady (GoogleMap googleMap) {
		mMap = googleMap;

		// Add a marker in Sydney and move the camera
		LatLng sydney = new LatLng (-34, 151);
		mMap.addMarker (new MarkerOptions ().position (sydney).title ("Marker in Sydney"));
		mMap.moveCamera (CameraUpdateFactory.newLatLng (sydney));
	}
}
