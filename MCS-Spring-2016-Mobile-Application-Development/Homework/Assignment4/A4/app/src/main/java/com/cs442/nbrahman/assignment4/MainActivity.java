package com.cs442.nbrahman.assignment4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<MenuItem> {
	ArrayList<MenuItem> lstResult;
	ListView lstMenu;
	Context context;
	private static LayoutInflater inflater=null;
	public CustomAdapter(Activity actSelected, int resource, ArrayList<MenuItem> arrlstAllMenuItems, ListView lstMenu) {
		super(actSelected, resource);
		this.lstMenu = lstMenu;
		lstResult=arrlstAllMenuItems;
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
		TextView lblPrice;
		CheckBox chkMenu;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder=new Holder();
		final View rowView;
		final MenuItem curMenuItem = lstResult.get(position);
		rowView = inflater.inflate(R.layout.menu_list, null);
		if (position % 2 == 1) {
			rowView.setBackgroundColor(Color.parseColor ("#F3F3F1"));
		} else {
			rowView.setBackgroundColor(Color.WHITE);
		}
		holder.img=(ImageView) rowView.findViewById (R.id.imageView1);
		holder.lblName=(TextView) rowView.findViewById(R.id.lblName);
		holder.lblPrice=(TextView) rowView.findViewById(R.id.lblPrice);
		holder.chkMenu=(CheckBox) rowView.findViewById(R.id.chkMenu);
		holder.lblName.setText(curMenuItem.name);
		holder.lblPrice.setText("$" + new DecimalFormat ("#0.00").format (curMenuItem.price));
		holder.img.setImageResource (curMenuItem.strImgName);
		holder.chkMenu.setOnCheckedChangeListener (new CheckBox.OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				lstMenu.setItemChecked (position, isChecked);
				lstResult.get(position).isSelected = isChecked;
			}
		});
		if (lstMenu.isEnabled ()) {
			holder.chkMenu.setChecked (curMenuItem.isSelected);
			holder.chkMenu.setVisibility (View.VISIBLE);
		}
		else
		{
			holder.chkMenu.setVisibility (View.INVISIBLE);
		}
		rowView.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				Toast.makeText (context, curMenuItem.name + "\t\tPrice: $" + curMenuItem.price + "\n\n" + curMenuItem.description , Toast.LENGTH_LONG).show();
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
								Toast.makeText(context,  curMenuItem.name + " removed from Menu.", Toast.LENGTH_LONG).show();
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

public class MainActivity extends AppCompatActivity {

	ListView lvMenu;
	Context context;
	CustomAdapter customAdaptor;
	double dblCumulativeOrderValue;
	ArrayList<SelMenuItem> lstSelMenuItems = new ArrayList<SelMenuItem>();

	public static ArrayList<MenuItem> arrlstAllMenuItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		context=this;
		setTitle("Welcome to MAD Restaurant");

		lvMenu=(ListView) findViewById(R.id.lvMenu);
		arrlstAllMenuItems = new ArrayList<MenuItem>();

		populateListView();
		customAdaptor = new CustomAdapter(this, R.layout.menu_list, arrlstAllMenuItems, lvMenu);
		lvMenu.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE);
		lvMenu.setAdapter (customAdaptor);

		dblCumulativeOrderValue = 0;

		View footerView = new View(context);
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics ());
		footerView.setMinimumHeight (px);
		lvMenu.addFooterView (footerView);

		Button btnPlaceOrder =(Button) findViewById (R.id.btnPlaceOrder);

		btnPlaceOrder.setOnClickListener
				(new View.OnClickListener () {
					@Override
					public void onClick (View v) {
						SparseBooleanArray arrSelItems = lvMenu.getCheckedItemPositions ();
						lstSelMenuItems = new ArrayList<SelMenuItem> ();
						for (int i = 0; i < arrlstAllMenuItems.size (); i++) {
							if (arrSelItems.get (i, false)) {
								SelMenuItem objSelMenuItem = new SelMenuItem ();
								objSelMenuItem.strName = arrlstAllMenuItems.get (i).name;
								objSelMenuItem.strDesc = arrlstAllMenuItems.get (i).description;
								objSelMenuItem.dblPrice = arrlstAllMenuItems.get (i).price;
								objSelMenuItem.strImgName = arrlstAllMenuItems.get (i).strImgName;
								objSelMenuItem.dblQty = 1;
								objSelMenuItem.dblAmount = arrlstAllMenuItems.get (i).price;
								lstSelMenuItems.add (objSelMenuItem);
							}
						}
						if (lstSelMenuItems.size () > 0) {
							arrSelItems = null;
							Intent intent = new Intent (context, OrderConfirmation.class);
							intent.putExtra ("lstSelMenuItems", lstSelMenuItems);

							/*for (int i = 0; i < arrlstAllMenuItems.size (); i++) {
								arrlstAllMenuItems.get (i).isSelected = false;
								lvMenu.setItemChecked (i, false);
							}*/
							startActivityForResult (intent, 0);
						} else {
							Toast.makeText (context, "Please select Menu items to be ordered", Toast.LENGTH_LONG).show ();
						}
					}
				});

		Button btnReset =(Button) findViewById (R.id.btnReset);
		btnReset.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				for (int i = 0; i < arrlstAllMenuItems.size (); i++) {
					arrlstAllMenuItems.get (i).isSelected = false;
					lvMenu.setItemChecked (i,false);
				}
				customAdaptor.notifyDataSetChanged ();
			}
		});

		Button btnOrderHistory =(Button) findViewById (R.id.btnOrderHistory);

		btnOrderHistory.setOnClickListener
				(new View.OnClickListener () {
					@Override
					public void onClick (View v) {
						OrderHelper ordHelper = new OrderHelper (context, "MAD_Order", 3);

						ArrayList<OrderMaster> arrlstOrders = ordHelper.getOrders ();

						if ((arrlstOrders != null) && (arrlstOrders.size()!=0)) {
							Intent intent = new Intent (context, OrderList.class);
							startActivity (intent);
						}
						else
						{
							Toast.makeText (context, "No orders placed!!",Toast.LENGTH_LONG).show();
						}
					}
				});

		FloatingActionButton menuAddButton = (FloatingActionButton) findViewById(R.id.fab);
		menuAddButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// custom dialog
				final Dialog dlgAddMenuDlg = new Dialog(context);
				dlgAddMenuDlg.setContentView(R.layout.activity_add_menu);
				dlgAddMenuDlg.setTitle("Add Menu Item To Menu");

				Button btnAddMenuCancel = (Button) dlgAddMenuDlg.findViewById(R.id.cancelBtn);
				// if button is clicked, close the custom dlgAddMenuDlg
				btnAddMenuCancel.setOnClickListener(new View.OnClickListener () {
					@Override
					public void onClick(View v) {
						dlgAddMenuDlg.dismiss();
					}
				});

				Button addMenuBtn = (Button) dlgAddMenuDlg.findViewById(R.id.addMenuBtn);
				addMenuBtn.setOnClickListener(new View.OnClickListener () {
					@Override
					public void onClick(View v) {
						String name = ((EditText) dlgAddMenuDlg.findViewById(R.id.nameEditView)).getText().toString();
						String description = ((EditText) dlgAddMenuDlg.findViewById(R.id.decriptionEditView)).getText().toString();
						String price = ((EditText) dlgAddMenuDlg.findViewById(R.id.priceEditText)).getText().toString();

						if(name.isEmpty()) {
							((EditText) dlgAddMenuDlg.findViewById(R.id.nameEditView)).setError("Please enter name.");
						} else if(description.isEmpty()) {
							((EditText) dlgAddMenuDlg.findViewById(R.id.decriptionEditView)).setError("Please enter description.");
						} else if(price.isEmpty()) {
							((EditText) dlgAddMenuDlg.findViewById(R.id.priceEditText)).setError("Please enter price.");
						} else {
							setItemInMenuDetails(name, description, price, R.drawable.breakfast);
							customAdaptor.notifyDataSetChanged();
							dlgAddMenuDlg.dismiss();
						}
					}
				});
				dlgAddMenuDlg.show ();
			}
		});
	}

	protected void onActivityResult (int RequestCode, int ResultCode, Intent data)
	{
		switch (RequestCode)
		{
			case 0:
				if (ResultCode == RESULT_OK)
				{
					dblCumulativeOrderValue += data.getDoubleExtra ("TotalOrderValue", 0);
				}
				TextView lblCumulativeTextView = (TextView)findViewById (R.id.lblCumulativeOrderValue);
				DecimalFormat df = new DecimalFormat ("#0.00");
				lblCumulativeTextView.setText ("Cumulative Order Value: $" + String.valueOf (df.format (dblCumulativeOrderValue)));
				break;
		}

		for (int i =0; i<arrlstAllMenuItems.size ();i++)
		{
			arrlstAllMenuItems.get (i).isSelected = false;
			lvMenu.setItemChecked (i, false);
		}
		customAdaptor.notifyDataSetChanged ();

		for (int i =0; i<lstSelMenuItems.size ();i++)
		{
			lstSelMenuItems.remove (i);
		}

		lstSelMenuItems = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return false;
	}

	private void populateListView()  {
		String [] names = {"Masala Dosa","Butter Chicken",
				"Vada Pav", "Scrambled Egg",
				"Chicken Afghani"};

		String [] description = {"Masala Dosa - Non spicy",
				"Mild spicy",
				"Spicy",
				"Depends",
				"Spicy"};
		String [] price = {"8.00","6.99", "4.69", "7.89", "11.00"};

		int [] imgNames = {R.drawable.masaladosa, R.drawable.butterchicken,
				R.drawable.vadapav, R.drawable.burji,
				R.drawable.afghanichicken};

		for(int i=0; i<names.length;i++) {
			setItemInMenuDetails (names[i], description[i], price[i], imgNames[i]);
		}
	}

	public void setItemInMenuDetails (String name, String description, String price, int imgName) {
		MenuItem curMenuItem = new MenuItem();
		curMenuItem.name = name;
		curMenuItem.description = description;
		curMenuItem.price = Double.parseDouble (price);
		curMenuItem.strImgName = imgName;

		arrlstAllMenuItems.add(curMenuItem);
	}

	public void removeMenuItem(int index) {
		arrlstAllMenuItems.remove(index);
		customAdaptor.notifyDataSetChanged();
	}
}


class MenuItem implements Serializable {
	public int strImgName;
	public String name;
	public String description;
	public double price;
	public boolean isSelected=false;
}