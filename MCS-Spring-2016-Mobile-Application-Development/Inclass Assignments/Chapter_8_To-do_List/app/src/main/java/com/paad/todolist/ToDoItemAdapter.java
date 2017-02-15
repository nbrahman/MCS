package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {

  int resource;
	Context activity;

  public ToDoItemAdapter(Context context,
                         int resource,
                         List<ToDoItem> items) {
    super(context, resource, items);
    this.resource = resource;
	  activity = context;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    LinearLayout todoView;

    ToDoItem item = getItem(position);

    final String taskString = item.getTask();
    Date createdDate = item.getCreated();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    String dateString = sdf.format(createdDate);

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

    dateView.setText(dateString);
    taskView.setText (taskString);

	todoView.setOnClickListener (new View.OnClickListener () {
		@Override
		public void onClick (View v) {
			String[] strSelectionArgs = new String[1];
			strSelectionArgs[0] = taskString;
			getContext ().getContentResolver ().delete (ToDoContentProvider.CONTENT_URI, "task=?", strSelectionArgs);
			(ToDoListActivity)activity.
		}
	});

    return todoView;
  }
}