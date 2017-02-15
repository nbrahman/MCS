package com.cs442.schafle.ass2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

        EditText Name;
        EditText Price;
        EditText Description;

        Button btnSubmit;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Inflate your View
            setContentView(R.layout.activity_main);

            // Get references to UI widgets
            final ListView myListView = (ListView)findViewById(R.id.myListView);
            //final EditText myEditText = (EditText)findViewById(R.id.myEditText);

            Name = (EditText) findViewById(R.id.myEditTextName);
            Price = (EditText) findViewById(R.id.myEditTextPrice);
            Description = (EditText) findViewById(R.id.myEditTextDesc);

            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            //btnSubmit.setOnClickListener(this);

            // Create the Array List of to do items
            final ArrayList<String> todoItems = new ArrayList<String>();

            // Create the Array Adapter to bind the array to the List View
            final ArrayAdapter<String> aa;

            aa = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    todoItems);

            // Bind the Array Adapter to the List View
            myListView.setAdapter(aa);


            btnSubmit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    todoItems.add(0, Name.getText().toString() + Price.getText().toString() + Description.getText().toString());
                    aa.notifyDataSetChanged();
                    //myEditText.setText("");
                }
            });



            // ListView Item Click Listener
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) myListView.getItemAtPosition(position);

                    // Show Alert

                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();

                }

            });

            myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int arg2, long arg3) {

                    // Can't manage to remove an item here
                    todoItems.remove(arg2);
                    Toast.makeText(getApplicationContext(), "Element" + Integer.toString(arg2) + "removed successfully!", Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
            });


        }

    }