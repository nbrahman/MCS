package com.cs442.nbrahman.assignment2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<MenuItem> {
	ArrayList<MenuItem> result;
	Context context;
	int [] imageId;
	private static LayoutInflater inflater=null;
	public CustomAdapter(MainActivity mainActivity, int resource, ArrayList<MenuItem> prgmNameList) {
		super(mainActivity, resource);
		result=prgmNameList;
		context=mainActivity;
		inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return result.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class Holder
	{
		TextView name;
		TextView price;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder=new Holder();
		final View rowView;
		final MenuItem item = result.get(position);
		rowView = inflater.inflate(R.layout.menu_list, null);
		holder.name=(TextView) rowView.findViewById(R.id.nameTextView);
		holder.price=(TextView) rowView.findViewById(R.id.priceTextView);
		holder.name.setText(item.name);
		holder.price.setText("$" + item.price);
		rowView.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				Toast.makeText (context, item.name + "\t\tPrice: $" + item.price + "\n\n" + item.description , Toast.LENGTH_LONG).show();
			}
		});

		rowView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(context)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("Are you sure you want to delete this Menu item?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((MainActivity) context).removeMenuItem(position);
								Toast.makeText(context,  item.name + " removed from Menu.", Toast.LENGTH_LONG).show();
							}

						})
						.setNegativeButton("No", null)
						.show();

				return false;
			}
		});
		return rowView;
	}

}

public class MainActivity extends AppCompatActivity {

	ListView lv;
	Context context;
	CustomAdapter customAdaptor;

	public static ArrayList<MenuItem> prgmNameList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		context=this;
		setTitle("Welcome to MAD Restaurant");

		lv=(ListView) findViewById(R.id.listView);
		prgmNameList = new ArrayList<MenuItem>();

		populateListView();
		customAdaptor = new CustomAdapter(this, R.layout.menu_list, prgmNameList);
		lv.setAdapter(customAdaptor);

		View footerView = new View(context);
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics ());
		footerView.setMinimumHeight(px);
		lv.addFooterView(footerView);

		FloatingActionButton menuAddButton = (FloatingActionButton) findViewById(R.id.fab);
		menuAddButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// custom dialog
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.activity_add_menu);
				dialog.setTitle("Add Menu Item To Menu");

				Button canclelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
				// if button is clicked, close the custom dialog
				canclelBtn.setOnClickListener(new View.OnClickListener () {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				Button addMenuBtn = (Button) dialog.findViewById(R.id.addMenuBtn);
				addMenuBtn.setOnClickListener(new View.OnClickListener () {
					@Override
					public void onClick(View v) {
						String name = ((EditText) dialog.findViewById(R.id.nameEditView)).getText().toString();
						String description = ((EditText) dialog.findViewById(R.id.decriptionEditView)).getText().toString();
						String price = ((EditText) dialog.findViewById(R.id.priceEditText)).getText().toString();

						if(name.isEmpty()) {
							((EditText) dialog.findViewById(R.id.nameEditView)).setError("Please enter name.");
						} else if(description.isEmpty()) {
							((EditText) dialog.findViewById(R.id.decriptionEditView)).setError("Please enter description.");
						} else if(price.isEmpty()) {
							((EditText) dialog.findViewById(R.id.priceEditText)).setError("Please enter price.");
						} else {
							setItemInMenuDetails(name, description, price);
							customAdaptor.notifyDataSetChanged();
							dialog.dismiss();
						}
					}
				});

				dialog.show();
			}
		});
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

		for(int i=0; i<names.length;i++) {
			setItemInMenuDetails (names[i], description[i], price[i]);
		}
	}

	public void setItemInMenuDetails (String name, String description, String price) {
		MenuItem item = new MenuItem();
		item.name = name;
		item.description = description;
		item.price = price;

		prgmNameList.add(item);
	}

	public void removeMenuItem(int index) {
		prgmNameList.remove(index);
		customAdaptor.notifyDataSetChanged();
	}
}


class MenuItem {
	public String name;
	public String description;
	public String price;
}