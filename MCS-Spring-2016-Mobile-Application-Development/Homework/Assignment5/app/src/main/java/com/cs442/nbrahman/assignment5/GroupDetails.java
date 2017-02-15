package com.cs442.nbrahman.assignment5;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GroupDetails extends AppCompatActivity {
	ContactHelper conHelper = new ContactHelper ();
	ArrayList<String> arrlstGroups = new ArrayList<> ();
	ArrayList<String> arrlstGroups2 = new ArrayList<> ();
	ArrayList arrlstGroupDet = new ArrayList ();
	Context context= this;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_group_details);
		arrlstGroups = conHelper.getAllGroups (getContentResolver ());

		for (int i=0;i<arrlstGroups.size();i++) {
			if (arrlstGroups.get (i) != "") {
				ArrayList<String> arrlstGroupMembers = conHelper.getAllGroupMembers (getContentResolver (), arrlstGroups.get (i).split ("\t")[0]);
				if (arrlstGroupMembers.size () > 0) {
					arrlstGroups2.add (arrlstGroups.get (i).split ("\t")[1]);
				}
			}
		}

		Spinner lstGroups = (Spinner) findViewById(R.id.lstGroups);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arrlstGroups);
		dataAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
		lstGroups.setAdapter (dataAdapter);

		lstGroups.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
				arrlstGroupDet = new ArrayList<> ();
				ArrayList<String> arrlstGroupMembers = conHelper.getAllGroupMembers (getContentResolver (), arrlstGroups.get(position).split ("\t")[0]);
				if (arrlstGroupMembers.size ()>0) {
					for (int k = 0; k < arrlstGroupMembers.size (); k++) {
						if (arrlstGroupMembers.get (k) != "") {
							arrlstGroupDet.add (arrlstGroupMembers.get (k).split ("\t")[0] +
									String.format("%" + String.valueOf(50 - arrlstGroupMembers.get (k).split ("\t")[0].length ()) + "s", " ") +
									arrlstGroupMembers.get (k).split ("\t")[1]);
						}
					}
				}
				if (arrlstGroupDet.size ()>0)
				{
					ListView lvGroups = (ListView)findViewById (R.id.lvGroups);
					ArrayAdapter<String> adapter = new MyAdapter(context, R.layout.activity_ind_group, arrlstGroupDet);

					lvGroups.setAdapter(adapter);;
				}
				else
				{
					Toast.makeText (context, "No groups details available!!!", Toast.LENGTH_LONG).show ();
				}
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {

			}
		});

		/*for (int i=0;i<arrlstGroups.size();i++)
		{
			if (arrlstGroups.get(i)!="") {
				ArrayList<String> arrlstGroupMembers = conHelper.getAllGroupMembers (getContentResolver (), arrlstGroups.get(i).split ("\t")[0]);
				if (arrlstGroupMembers.size ()>0) {
					arrlstGroupDet.add (arrlstGroups.get(i).split ("\t")[1]);
					for (int k = 0; k < arrlstGroupMembers.size (); k++) {
						if (arrlstGroupMembers.get (k) != "") {
							arrlstGroupDet.add ("\t\t" + arrlstGroupMembers.get (k).split ("\t")[0] +
									String.format("%" + String.valueOf(50 - arrlstGroupMembers.get (k).split ("\t")[0].length ()) + "s", " ") +
									arrlstGroupMembers.get (k).split ("\t")[1]);
						}
					}
				}
			}
		}
		if (arrlstGroupDet.size ()>0)
		{
			ListView lvGroups = (ListView)findViewById (R.id.lvGroups);
			ArrayAdapter<String> adapter = new MyAdapter(this, R.layout.activity_ind_group, arrlstGroupDet);

			lvGroups.setAdapter(adapter);;
		}
		else
		{
			Toast.makeText (this, "No groups details available!!!", Toast.LENGTH_LONG).show ();
		}*/

		Button btnBack = (Button)findViewById (R.id.btnBack);
		btnBack.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				onBackPressed ();
			}
		});
	}

	class MyAdapter extends ArrayAdapter
	{
		private Context mContext;
		private int id;
		private List <String>items ;
		private LayoutInflater inflater=null;

		public MyAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			mContext = context;
			id = textViewResourceId;
			items = objects;
			inflater = (LayoutInflater) mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View rowView;
			rowView = inflater.inflate(R.layout.activity_ind_group, null);

			if (position % 2 == 1) {
				rowView.setBackgroundColor(Color.parseColor ("#F3F3F1"));
			} else {
				rowView.setBackgroundColor(Color.WHITE);
			}

			TextView tv = (TextView)rowView.findViewById (R.id.textView);
			tv.setText (arrlstGroupDet.get(position).toString ());

			return rowView;
		}
	}
}
