package com.cs442.nbrahman.feb_20_2016_app1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	ListAdapter adapter;
	ListView lvList;
	ArrayList<String> arrlstStrings;
	EditText editText;
	Button btnAdd;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		SharedPreferences aP = getSharedPreferences ("My", MODE_PRIVATE);
		String txtContent = aP.getString ("txtValue", null);
		Log.d ("My", txtContent);
		if (savedInstanceState != null && savedInstanceState.containsKey ("ARRAY_LIST")) {
			arrlstStrings = savedInstanceState.getStringArrayList ("ARRAY_LIST");
		}
		else
			arrlstStrings = new ArrayList<String> ();
		lvList = (ListView) findViewById (R.id.listView);
		adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, arrlstStrings);
		lvList.setAdapter (adapter);
		editText = (EditText)findViewById (R.id.editText);
		editText.setText (txtContent);
		btnAdd = (Button)findViewById (R.id.btnAdd);
		btnAdd.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				arrlstStrings.add (editText.getText ().toString ());
				((BaseAdapter)lvList.getAdapter ()).notifyDataSetChanged ();
				editText.setText ("");
			}
		});
	}

	@Override
	public void onSaveInstanceState (Bundle saveInstanceState) {
		saveInstanceState.putStringArrayList ("ARRAY_LIST", arrlstStrings);
		super.onSaveInstanceState (saveInstanceState);
	}

	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	protected void onPause () {
		SharedPreferences.Editor editor = getSharedPreferences ("My", MODE_PRIVATE).edit ();
		EditText editText = (EditText) findViewById (R.id.editText);
		editor.putString ("txtValue",editText.getText ().toString ());
		editor.apply ();
		super.onPause ();
	}
}
