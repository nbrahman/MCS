package com.cs442.nbrahman.assignment5;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {

	ArrayList<ContactData> arrlstAllContacts = new ArrayList<>();
	MyAdapter ma;
	Context context;
	ContactHelper ch = new ContactHelper ();
	private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
	private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS=2;
	int permissionCheckReadContact=-1;
	int permissionCheckWriteContact=-1;

	public void getReadPermission()
	{
		permissionCheckReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

		if (permissionCheckReadContact==-1) {
			// Here, thisActivity is the current activity
			if (ContextCompat.checkSelfPermission (this,
					Manifest.permission.READ_CONTACTS)
					!= PackageManager.PERMISSION_GRANTED) {

				// Should we show an explanation?
				if (ActivityCompat.shouldShowRequestPermissionRationale (this,
						Manifest.permission.READ_CONTACTS)) {

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.

				} else {

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions (this,
							new String[]{Manifest.permission.READ_CONTACTS},
							MY_PERMISSIONS_REQUEST_READ_CONTACTS);

					// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			}
		}
	}

	public void getWritePermission()
	{
		permissionCheckWriteContact = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);

		if (permissionCheckWriteContact==-1) {
			// Here, thisActivity is the current activity
			if (ContextCompat.checkSelfPermission (this,
					Manifest.permission.WRITE_CONTACTS)
					!= PackageManager.PERMISSION_GRANTED) {

				// Should we show an explanation?
				if (ActivityCompat.shouldShowRequestPermissionRationale (this,
						Manifest.permission.WRITE_CONTACTS)) {

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.

				} else {

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions (this,
							new String[]{Manifest.permission.WRITE_CONTACTS},
							MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);

					// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			}
		}
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		getReadPermission ();
		getWritePermission ();
		if ((permissionCheckReadContact==0) && (permissionCheckWriteContact==0)) {
			super.onCreate (savedInstanceState);
			setContentView (R.layout.activity_main);
			context = this;

			Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
			setSupportActionBar (toolbar);

			FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);

			fab.setOnClickListener (new View.OnClickListener () {
				@Override
				public void onClick (View v) {
					// custom dialog
					final Dialog dlgAddMenuDlg = new Dialog (context);
					dlgAddMenuDlg.setContentView (R.layout.activity_add_contact);
					dlgAddMenuDlg.setTitle ("Add Contact");

					Button btnAddMenuCancel = (Button) dlgAddMenuDlg.findViewById (R.id.cancelBtn);
					// if button is clicked, close the custom dlgAddMenuDlg
					btnAddMenuCancel.setOnClickListener (new View.OnClickListener () {
						@Override
						public void onClick (View v) {
							dlgAddMenuDlg.dismiss ();
						}
					});

					Button addMenuBtn = (Button) dlgAddMenuDlg.findViewById (R.id.addMenuBtn);
					addMenuBtn.setText ("Add Contact");
					addMenuBtn.setOnClickListener (new View.OnClickListener () {
						@Override
						public void onClick (View v) {
							String strName = ((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactName)).getText ().toString ();
							String strPhone = ((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactPhone)).getText ().toString ();

							if (strName.isEmpty ()) {
								((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactName)).setError ("Please enter name.");
							} else if (strPhone.isEmpty ()) {
								((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactPhone)).setError ("Please enter description.");
							} else {
								boolean flgAddContact = ch.insertContact (getContentResolver (), strName, strPhone);
								dlgAddMenuDlg.dismiss ();
								if (flgAddContact) {
									arrlstAllContacts.clear ();
									arrlstAllContacts.addAll (ch.getContactCursor (getContentResolver (), null));
									ma.notifyDataSetChanged ();
								}
							}
						}
					});
					dlgAddMenuDlg.show ();
				}
			});

			//Code to fetch all contacts and bind it to list view
			arrlstAllContacts = ch.getContactCursor (getContentResolver (), null);

			if (arrlstAllContacts.size ()!=0) {
				ListView lv = (ListView) findViewById (R.id.lvContacts);
				ma = new MyAdapter ();
				lv.setAdapter (ma);
				lv.setOnItemClickListener (this);
				lv.setItemsCanFocus (false);
				lv.setTextFilterEnabled (true);
			}
			else
			{
				Toast.makeText (this, "No contacts to display!!", Toast.LENGTH_LONG).show ();
			}
			Button btnGroups =(Button) findViewById (R.id.btnGroups);

			btnGroups.setOnClickListener
					(new View.OnClickListener () {
						@Override
						public void onClick (View v) {
							ContactHelper conHelper = new ContactHelper();

							ArrayList<String> arrlstGroups = conHelper.getAllGroups (getContentResolver ());

							if ((arrlstGroups != null) && (arrlstGroups.size()!=0)) {
								Intent intent = new Intent (context, GroupDetails.class);
								startActivity (intent);
							}
							else
							{
								Toast.makeText (context, "No Groups with contacts!!",Toast.LENGTH_LONG).show();
							}
						}
					});

		}
		else if ((permissionCheckReadContact==-1) && (permissionCheckWriteContact==-1)) {
			Toast.makeText (this, "App needs both Read and Write permissions on Contacts to work properly.\nPlease allow both the permissions",Toast.LENGTH_LONG).show();
		}
		else if ((permissionCheckReadContact==0) && (permissionCheckWriteContact==-1)) {
			Toast.makeText (this, "App needs both Read and Write permissions on Contacts to work properly.\nPlease allow Write permissions",Toast.LENGTH_LONG).show();
		}
		else if ((permissionCheckReadContact==-1) && (permissionCheckWriteContact==0)) {
			Toast.makeText (this, "App needs both Read and Write permissions on Contacts to work properly.\nPlease allow Read permissions",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					permissionCheckReadContact = 0;
					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					permissionCheckReadContact = -1;

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					permissionCheckWriteContact = 0;
					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					permissionCheckWriteContact = -1;

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ma.toggle (arg2);
	}

	class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
	{
		private SparseBooleanArray mCheckStates;
		LayoutInflater mInflater;
		TextView tv1,tv;
		ImageView ivPhoto;
		//CheckBox cb;
		MyAdapter()
		{
			mCheckStates = new SparseBooleanArray(arrlstAllContacts.size());
			mInflater = (LayoutInflater)MainActivity.this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return arrlstAllContacts.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View vi=convertView;
			if(convertView==null) {
				vi = mInflater.inflate (R.layout.item_contact, null);
			}

			tv= (TextView) vi.findViewById(R.id.lblName);
			tv1= (TextView) vi.findViewById(R.id.lblPhoneNo);
			ivPhoto = (ImageView)vi.findViewById (R.id.imageView1);
			//cb = (CheckBox) vi.findViewById(R.id.chkContact);

			if (position % 2 == 1) {
				vi.setBackgroundColor(Color.parseColor ("#F3F3F1"));
			} else {
				vi.setBackgroundColor(Color.WHITE);
			}

			tv.setText(arrlstAllContacts.get(position).strContactName);
			tv1.setText(arrlstAllContacts.get(position).strPhoneNo);
			if (arrlstAllContacts.get(position).strPhoto!=null)
			{
				try
				{
					ivPhoto.setImageBitmap ((MediaStore.Images.Media.getBitmap (getContentResolver (), Uri.parse (arrlstAllContacts.get(position).strPhoto))));
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			else
			{
				ivPhoto.setImageResource (R.drawable.ic_launcher);
			}
			//cb.setTag(position);
			//cb.setChecked(mCheckStates.get(position, false));
			//cb.setOnCheckedChangeListener(this);

			vi.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					new AlertDialog.Builder(context)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("Are you sure you want to delete this Contact?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ContactHelper ch = new ContactHelper ();
								boolean flgDelContact = ch.deleteContact (getContentResolver (), /*arrlstAllContacts.get (position).strPhoneNo, */arrlstAllContacts.get(position));

								if (flgDelContact) {
									//Code to fetch all contacts and bind it to list view
									Toast.makeText(context,
											arrlstAllContacts.get(position).strContactName + "\n" + arrlstAllContacts.get(position).strPhoneNo +
													" removed from Contacts", Toast.LENGTH_LONG).show ();

									/*ListView lv= (ListView) findViewById(R.id.lvContacts);
									ma = new MyAdapter();
									lv.setAdapter(ma);
									lv.setOnItemClickListener(MainActivity.this);
									lv.setItemsCanFocus(false);
									lv.setTextFilterEnabled (true);*/
									arrlstAllContacts.clear ();
									arrlstAllContacts.addAll (ch.getContactCursor (getContentResolver (), null));
									ma.notifyDataSetChanged ();
								}
							}
						})
						.setNegativeButton("No", null)
						.show();
					return false;
				}
			});

			vi.setOnClickListener (new View.OnClickListener () {
				@Override
				public void onClick (View v)
				{
					new AlertDialog.Builder(context)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("Are you sure you want to update this Contact?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								// custom dialog
								final Dialog dlgAddMenuDlg = new Dialog (context);
								dlgAddMenuDlg.setContentView (R.layout.activity_add_contact);
								dlgAddMenuDlg.setTitle ("Edit Contact");

								EditText evNewName = (EditText)dlgAddMenuDlg.findViewById (R.id.txtAddContactName);
								EditText evNewPhoneNo = (EditText)dlgAddMenuDlg.findViewById (R.id.txtAddContactPhone);

								evNewName.setText (arrlstAllContacts.get(position).strContactName);
								evNewPhoneNo.setText (arrlstAllContacts.get(position).strPhoneNo);

								evNewName.setFocusable (false);
								evNewName.setClickable (true);

								Button btnAddMenuCancel = (Button) dlgAddMenuDlg.findViewById (R.id.cancelBtn);
								// if button is clicked, close the custom dlgAddMenuDlg
								btnAddMenuCancel.setOnClickListener (new View.OnClickListener ()
								{
									@Override
									public void onClick (View v) {
										dlgAddMenuDlg.dismiss ();
									}
								});

								Button modContactBtn = (Button) dlgAddMenuDlg.findViewById (R.id.addMenuBtn);
								modContactBtn.setText ("Update Contact");
								modContactBtn.setOnClickListener (new View.OnClickListener ()
								{
									@Override
									public void onClick (View v)
									{
										String strNewName = ((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactName)).getText ().toString ();
										String strNewPhone = ((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactPhone)).getText ().toString ();

										if (strNewName.isEmpty ())
										{
											((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactName)).setError ("Please enter name.");
										}
										else if (strNewPhone.isEmpty ())
										{
											((EditText) dlgAddMenuDlg.findViewById (R.id.txtAddContactPhone)).setError ("Please enter description.");
										}
										else
										{
											boolean flgModContact = ch.updateContact (getContentResolver (),
													strNewName, strNewPhone,
													arrlstAllContacts.get (position).strContactName, arrlstAllContacts.get (position).strPhoneNo,
													arrlstAllContacts.get (position).strID);
											dlgAddMenuDlg.dismiss ();
											if (flgModContact) {
												arrlstAllContacts.clear ();
												arrlstAllContacts.addAll (ch.getContactCursor (getContentResolver (), null));
												ma.notifyDataSetChanged ();
											}
										}
									}
								});
								dlgAddMenuDlg.show ();
							}
						})
							.setNegativeButton("No", null)
							.show();
				}
			});

			return vi;
		}

		public boolean isChecked(int position)
		{
			return mCheckStates.get(position, false);
		}

		public void setChecked(int position, boolean isChecked) {
			mCheckStates.put(position, isChecked);
			System.out.println("hello...........");
			notifyDataSetChanged();
		}

		public void toggle(int position) {
			setChecked(position, !isChecked(position));
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			mCheckStates.put ((Integer) buttonView.getTag (), isChecked);
		}
	}
}

class ContactData
{
	String strContactName;
	String strPhoneNo;
	String strID;
	String strPhoto;
	boolean hasEmail=false;
}

class ContactHelper
{
	public ArrayList<ContactData> getContactCursor(ContentResolver contactHelper, String startsWith)
	{
		ArrayList<ContactData> arrlstCD = new ArrayList<> ();
		String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID,//._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
		Cursor cur;

		try {
			if (startsWith != null && !startsWith.equals("")) {
				cur = contactHelper.query(
						ContactsContract.Data.CONTENT_URI,
						projection,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
								+ " like \"" + startsWith + "%\" AND "
								+ ContactsContract.CommonDataKinds.Phone.NUMBER + "!=\"\"", null,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
								+ " ASC");
			} else {
				cur = contactHelper.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						projection, ContactsContract.CommonDataKinds.Phone.NUMBER + "!=\"\"", null,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
								+ " ASC");

			}
			if (cur != null) {
				DatabaseUtils.dumpCursor (cur);
				//cur.moveToFirst ();
				while (cur.moveToNext ()) {
					ContactData curContact = new ContactData ();
					curContact.strContactName = cur.getString (cur.getColumnIndex (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					curContact.strPhoneNo = cur.getString (cur.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER));
					curContact.strID = cur.getString (cur.getColumnIndex (ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
					curContact.strPhoto = cur.getString (cur.getColumnIndex (ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

					String[] emailProjection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID,};

					Cursor curEmail = contactHelper.query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							emailProjection,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
							new String[]{curContact.strID}, null);
					if (curEmail.getCount ()>0)
					{
						curContact.hasEmail = true;
					}
					arrlstCD.add (curContact);
				}
				cur.close ();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arrlstCD;
	}

	private long getContactID(ContentResolver contactHelper, String strID)
	{
		Uri contactUri = Uri.withAppendedPath (ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode (strID));

		String[] projection = { ContactsContract.PhoneLookup._ID };
		Cursor cursor = null;

		try
		{
			cursor = contactHelper.query(contactUri, projection, null, null,
					null);

			if (cursor!= null)
			{
				if (cursor.moveToFirst()) {
					int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
					return cursor.getLong(personID);
				}
			}

			return -1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return -1;
	}

	public boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<>();
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference (ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue (
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(
						ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
						firstName).build ());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue (
						ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue (ContactsContract.CommonDataKinds.Phone.NUMBER,
						mobileNumber)
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
						ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build ());

		try {
			contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean updateContact(ContentResolver contactAdder, String newfirstName, String newmobileNumber,
	                             String oldfirstname, String oldmobilenumber, String strContactID) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<>();
		String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
				ContactsContract.Data.MIMETYPE + " = ? AND " +
				String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";

		String where2 = ContactsContract.Data.CONTACT_ID + " = ?";

		String[] params = new String[] {oldfirstname,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
				String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};

		String[] params2 = new String[] {strContactID};

		ops.add(ContentProviderOperation.newUpdate (ContactsContract.Data.CONTENT_URI)
				.withSelection (where, params)
				.withValue(ContactsContract.CommonDataKinds.Phone.DATA, newmobileNumber)
				.build ());

		ops.add (ContentProviderOperation.newUpdate (ContactsContract.Data.CONTENT_URI)
				.withSelection (where2, params2)
				.withValue (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, newfirstName)
				.build ());

		try
		{
			contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
		}

		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	public boolean deleteContact(ContentResolver contactHelper, /*String number*/ ContactData delContact)
	{

		boolean flgDataExists = false;

		ArrayList<ContentProviderOperation> ops = new ArrayList<>();
		String[] args = new String[] { String.valueOf(/*getContactID (
				contactHelper, number)*/delContact.strID)};

		/*Uri contactUri = Uri.withAppendedPath (ContactsContract.Data.CONTENT_URI,
			Uri.encode (args[0]));

		String[] projection = { ContactsContract.CommonDataKinds.Email._ID };
		Cursor curDelRecs;

		curDelRecs = contactHelper.query(contactUri, null, null, null, null);

		if (curDelRecs!= null)
		{
			if (curDelRecs.getCount ()>0) {
				flgDataExists = true;
			}
			curDelRecs.close ();
		}

		if (!flgDataExists) {
			contactUri = Uri.withAppendedPath (ContactsContract.Data.CONTENT_URI,
				Uri.encode (args[0]));
			projection[0] = ContactsContract.CommonDataKinds.Phone._ID;

			curDelRecs = contactHelper.query (contactUri, null, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Website._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.StructuredPostal._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.StructuredName._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.SipAddress._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Relation._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Organization._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Note._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Nickname._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Identity._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.GroupMembership._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Event._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Contactables._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.CommonDataKinds.Callable._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.RawContacts.Data._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.SyncState._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.Profile._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.Groups._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.Directory._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.Data._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}

		if (!flgDataExists) {
			projection[0] = ContactsContract.Contacts._ID;

			curDelRecs = contactHelper.query (contactUri, projection, null, null, null);

			if (curDelRecs!= null) {
				if (curDelRecs.getCount () > 0) {
					flgDataExists = true;
				}
				curDelRecs.close ();
			}
		}*/

		if (!delContact.hasEmail) {
			ops.add (ContentProviderOperation.newDelete (ContactsContract.RawContacts.CONTENT_URI)
					.withSelection (ContactsContract.RawContacts.CONTACT_ID + "=?", args).build ());
			/*try {
				contactHelper.applyBatch (ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace ();
				return false;
			} catch (OperationApplicationException e) {
				e.printStackTrace ();
				return false;
			}*/
			return true;
		}
		else
		{
			return updateContact (contactHelper, delContact.strContactName, "", delContact.strContactName, delContact.strPhoneNo, delContact.strID);
		}
	}

	public ArrayList<String> getAllGroups(ContentResolver cr) {

		ArrayList<String> arrlstGroupNames = new ArrayList<> ();

		Cursor phones = cr.query(ContactsContract.Groups.CONTENT_URI, null,
				ContactsContract.Groups.DELETED+"!='1' AND "+
				ContactsContract.Groups.GROUP_VISIBLE+"!='0'", null, ContactsContract.Groups.TITLE + " ASC");
		if (phones!=null)
		{
			while (phones.moveToNext())
			{
				arrlstGroupNames.add (String.valueOf (phones.getString (phones.getColumnIndex (ContactsContract.Groups._ID))) + "\t" + phones.getString (phones.getColumnIndex (ContactsContract.Groups.TITLE)));
			}
			phones.close ();
		}
		return arrlstGroupNames;
	}

	public ArrayList<String> getAllGroupMembers(ContentResolver cr, String strGroupID) {

		ArrayList<String> arrlstGroupMembers = new ArrayList<> ();
		String[] cProjection = { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

		Cursor groupCursor = cr.query(
				ContactsContract.Data.CONTENT_URI,
				cProjection,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "= ?" + " AND "
						+ ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
						+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'",
				new String[]{strGroupID}, null);
		if (groupCursor != null && groupCursor.moveToFirst())
		{
			do
			{

				int nameCoumnIndex = groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

				String name = groupCursor.getString(nameCoumnIndex);

				long contactId = groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));

				Cursor numberCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

				if (numberCursor.moveToFirst())
				{
					int numberColumnIndex = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					do
					{
						String phoneNumber = numberCursor.getString(numberColumnIndex);
						arrlstGroupMembers.add (name + "\t" + phoneNumber);
					} while (numberCursor.moveToNext());
					numberCursor.close();
				}
			} while (groupCursor.moveToNext());
			groupCursor.close();
		}
		return arrlstGroupMembers;
	}

}