package com.cs442.nbrahman.assignment5_phonebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
	ListView lvContacts;
	Activity context;
	CustomAdapter customAdaptor;


	public static ArrayList<ContactData> arrlstAllContacts;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		context=this;
		Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
		setSupportActionBar (toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
		fab.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View view) {
				Snackbar.make (view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction ("Action", null).show ();
			}
		});

		LoadContactsAyscn lca = new LoadContactsAyscn();
		lca.execute();

		lvContacts = (ListView) findViewById(R.id.lstContacts);

	}

	class LoadContactsAyscn extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pd = ProgressDialog.show(MainActivity.this, "Loading Contacts",
					"Please Wait");
		}

		@Override
		protected Void doInBackground (Void... params) {

			Cursor c = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
			while (c.moveToNext()) {
				ContactData cdCurrent = new ContactData();
				cdCurrent.strContactName = c.getString (
						c.getColumnIndex (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				cdCurrent.strPhoneNo = c.getString(
						c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				cdCurrent.strID = c.getString (c.getColumnIndex (ContactsContract.CommonDataKinds.Phone._ID));
				arrlstAllContacts.add(cdCurrent);

			}
			c.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute (v);

			pd.cancel();

			customAdaptor = new CustomAdapter(context, R.layout.text, arrlstAllContacts, lvContacts);
			lvContacts.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE);
			lvContacts.setAdapter (customAdaptor);

			/*ArrayAdapter<ContactData> adapter = new ArrayAdapter<> (
					getApplicationContext(), R.layout.text, arrlstContacts);

			list.setAdapter(adapter);*/

		}

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId ();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected (item);
	}
}

class ContactData
{
	String strContactName;
	String strPhoneNo;
	String strID;
}

class CustomAdapter extends ArrayAdapter<ContactData> {
	ArrayList<ContactData> lstResult;
	ListView lstContacts;
	Context context;
	private static LayoutInflater inflater=null;
	public CustomAdapter(Activity actSelected, int resource, ArrayList<ContactData> arrlstAllContacts, ListView lstContacts) {
		super(actSelected, resource);
		this.lstContacts = lstContacts;
		lstResult=arrlstAllContacts;
		context=actSelected;
		inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return lstResult.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class Holder
	{
		ImageView img;
		TextView lblName;
		TextView lblPhoneNo;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder=new Holder();
		final View rowView;
		final ContactData curContact = lstResult.get(position);
		rowView = inflater.inflate(R.layout.text, null);
		if (position % 2 == 1) {
			rowView.setBackgroundColor(Color.parseColor ("#F3F3F1"));
		} else {
			rowView.setBackgroundColor(Color.WHITE);
		}
		holder.img=(ImageView) rowView.findViewById (R.id.imageView1);
		holder.lblName=(TextView) rowView.findViewById(R.id.lblName);
		holder.lblPhoneNo=(TextView) rowView.findViewById(R.id.lblPhoneNo);
		holder.lblName.setText (curContact.strContactName);
		holder.lblPhoneNo.setText (curContact.strPhoneNo);
		/*holder.chkMenu.setOnCheckedChangeListener (new CheckBox.OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				lstContacts.setItemChecked (position, isChecked);
				lstResult.get(position).isSelected = isChecked;
			}
		});
		if (lstContacts.isEnabled ()) {
			holder.chkMenu.setChecked (curContact.isSelected);
			holder.chkMenu.setVisibility (View.VISIBLE);
		}
		else
		{
			holder.chkMenu.setVisibility (View.INVISIBLE);
		}*/
		rowView.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				Toast.makeText (context, curContact.strContactName + "\t\tPhone No.: " + curContact.strPhoneNo, Toast.LENGTH_LONG).show();
			}
		});

		/*rowView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(context)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("Are you sure you want to delete this Menu Menu item?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((MainActivity) context).removeMenuItem(position);
								Toast.makeText(context,  curContact.name + " removed from Menu.", Toast.LENGTH_LONG).show();
							}
						})
						.setNegativeButton("No", null)
						.show();

				return false;
			}
		});*/
		return rowView;
	}
}

