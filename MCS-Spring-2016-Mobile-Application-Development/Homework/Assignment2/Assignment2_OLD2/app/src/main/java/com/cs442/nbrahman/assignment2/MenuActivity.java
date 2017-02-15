package com.cs442.nbrahman.assignment2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Niks on 2/13/2016.
 */
public class MenuActivity extends AppCompatActivity {

	EditText myEditTextName;
	EditText myEditTextDesc;
	EditText myEditTextPrice;
	Button myButtonAdd;
	ListView myListView;

	public void onCreate (Bundle savedInstanceState) {
		// Create the Array Adapter to bind the array to the List View
		final ArrayAdapter<String> aa;

		// Create the Array List of menu items
		final ArrayList<String> menuItems;
		super.onCreate (savedInstanceState);

		// Inflate your View
		setContentView (R.layout.main);

		// Create the Array List of menu items
		menuItems = new ArrayList<String> ();

		myEditTextName = (EditText) findViewById (R.id.myEditTextName);
		myEditTextDesc = (EditText) findViewById (R.id.myEditTextDesc);
		myEditTextPrice = (EditText) findViewById (R.id.myEditTextPrice);
		myButtonAdd = (Button) findViewById (R.id.btnAdd);
		myListView = (ListView) findViewById (R.id.myListView);

		// initialize the string variables
		final String strName = myEditTextName.getText ().toString ();
		final String strDesc = myEditTextDesc.getText ().toString ();
		final String strPrice = myEditTextPrice.getText ().toString ();

		aa = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, menuItems);

		// Bind the Array Adapter to the List View
		myListView.setAdapter (aa);

		myButtonAdd.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				Log.d ("Button", "onClick: ");
				menuItems.add (0, strName + "\t" + strDesc + "\t" + strPrice);
				aa.notifyDataSetChanged ();
			}
		});

		myListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick (AdapterView<?> parent, View view,
			                         int position, long id) {
				// ListView Clicked item value
				String itemValue = (String) myListView.getItemAtPosition (position);

				// Show Alert
				Toast.makeText (getApplicationContext (), itemValue, Toast.LENGTH_LONG).show ();
			}
		});


		myListView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener () {

			@Override
			public boolean onItemLongClick (AdapterView<?> parent, View view,
			                                int arg2, long arg3) {
				// ListView Clicked item value
				String itemValue = (String) myListView.getItemAtPosition (arg2);
				String strName = itemValue.split ("\t")[0];

				menuItems.remove (arg2);
				Toast.makeText (getApplicationContext (), strName + "removed from menu", Toast.LENGTH_LONG)
						.show ();
				return false;
			}
		});
	}
}