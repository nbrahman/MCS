package com.cs442.nbrahman.assignment2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Niks on 2/13/2016.
 */
public class MenuItemAdapter extends ArrayAdapter<MenuItem>
{

	int resource;

	public MenuItemAdapter(Context context,
	                       int resource,
	                       List<MenuItem> items) {
		super(context, resource, items);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout todoView;
		MenuItem item = getItem(position);

		String taskString = item.getTask();
		Date createdDate = item.getCreated();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateString = sdf.format(createdDate);
		int num = item.getNum();
		if (convertView == null) {
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, todoView, true);
		} else {
			todoView = (LinearLayout) convertView;
		}

		TextView dateView = (TextView)todoView.findViewById(R.id.rowDate);
		TextView taskView = (TextView)todoView.findViewById(R.id.row);
		TextView NumView = (TextView)todoView.findViewById(R.id.rowNum);

		dateView.setText(dateString + " " + item.getTimeString());
		taskView.setText(taskString);
		StringBuilder aStr = new StringBuilder();
		aStr.append(num);
		NumView.setText(aStr.toString());

		return todoView;
	}
}