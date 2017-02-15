package com.paad.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener {
  
  private ArrayList<ToDoItem> todoItems;
  private ToDoItemAdapter aa;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Inflate your view
    setContentView(R.layout.main);
      
    // Get references to the Fragments
    FragmentManager fm = getFragmentManager();
    ToDoListFragment todoListFragment = 
      (ToDoListFragment)fm.findFragmentById(R.id.TodoListFragment);
     
    // Create the array list of to do items
    todoItems = new ArrayList<ToDoItem>();


    //Load(getCurrentFocus());
    // Create the array adapter to bind the array to the ListView
    int resID = R.layout.todolist_item;
    aa = new ToDoItemAdapter(this, resID, todoItems);
     
    // Bind the array adapter to the ListView.
    todoListFragment.setListAdapter(aa);
  }
  
  public void onNewItemAdded(String newItem) {
    ToDoItem newTodoItem = new ToDoItem(newItem);
    todoItems.add(0, newTodoItem);
    aa.notifyDataSetChanged();
  }

  public int getCount(){
    return todoItems.size();
  }

  ArrayList<ToDoItem> getToDoItemArrayListFromStringArrayList(ArrayList<String> aString)
  {
    ArrayList<ToDoItem> result;
    result= new ArrayList<ToDoItem>();
    Date d;
    for(String aStringValue :aString) {
      String t = aStringValue.substring(aStringValue.lastIndexOf(")") + 1);
      Log.d("ToDo:", "task=(" + t  + ")");
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

      ToDoItem aTValue;
      aTValue = new ToDoItem(t,d);
      Log.d("ToDo:", "read toDoItem=" + aTValue.toString());
      result.add(0, aTValue);
    }
    return result;
  }

  ArrayList<String> getStringArrayListFromToDoItemArrayList(ArrayList<ToDoItem> aToDoItem)
  {
    ArrayList<String> result = new ArrayList<String>();
    Date d;
    for(ToDoItem aToDoItemValue :aToDoItem) {
      Log.d("ToDo:", "save toDoItem=" + aToDoItemValue.toString());

      result.add(aToDoItemValue.toString());
    }
    return result;
  }
}