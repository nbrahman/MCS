package com.cs442.nbrahman.assignment2;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niks on 2/13/2016.
 */
public class MenuActivity extends Activity implements NewItemFragment.OnNewItemAddedListener
{
	private ArrayList<MenuItem> menuItems;
	private MenuItemAdapter aa;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Inflate your view
		setContentView(R.layout.activity_main);

		// Get references to the Fragments
		FragmentManager fm = getFragmentManager();
		MenuFragment menuFragment =
				(MenuFragment)fm.findFragmentById(R.id.MenuFragment);

		// Create the array list of to do items
		menuItems = new ArrayList<MenuItem>();


		//Load(getCurrentFocus());
		// Create the array adapter to bind the array to the ListView
		int resID = R.layout.menu_item_layout;
		aa = new MenuItemAdapter(this, resID, menuItems);

		// Bind the array adapter to the ListView.
		MenuFragment.setListAdapter(aa);
	}

	public void onNewItemAdded(String newItem) {
		MenuItem newMenuItem = new MenuItem (newItem);
		menuItems.add(0, newMenuItem);
		aa.notifyDataSetChanged();
	}

	public int getCount(){
		return menuItems.size();
	}

	ArrayList<MenuItem> getMenuItemArrayListFromStringArrayList(ArrayList<String> aString)
	{
		ArrayList<MenuItem> result;
		result= new ArrayList<MenuItem>();
		Date d;
		for(String aStringValue :aString) {
			String t = aStringValue.substring(aStringValue.lastIndexOf(")") + 1);
			Log.d ("ToDo:", "task=(" + t + ")");
			String dStr = aStringValue.substring(aStringValue.lastIndexOf("(")+1,
					aStringValue.lastIndexOf(")"));
			Log.d("ToDo:", "date=(" + dStr + ")");
			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:hh");
			try {
				d = sdf.parse(dStr);
				Log.d("ToDo:", "try(date, task)=(" + t + "," + dStr + ")");

			}
			catch (Exception e)
			{
				Log.d("ToDo:", "catch(date, task)=(" + t + "," + dStr + ")");
				continue;
			}

			MenuItem aTValue;
			aTValue = new MenuItem(t,d);
			Log.d("ToDo:", "read toDoItem=" + aTValue.toString());
			result.add(0, aTValue);
		}
		return result;
	}

	ArrayList<String> getStringArrayListFromMenuItemArrayList(ArrayList<MenuItem> aMenuItem)
	{
		ArrayList<String> result = new ArrayList<String>();
		Date d;
		for(MenuItem aMenuItemValue :aMenuItem) {
			Log.d("ToDo:", "save toDoItem=" + aMenuItemValue.toString());

			result.add(aMenuItemValue.toString());
		}
		return result;
	}
}