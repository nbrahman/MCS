package com.cs442.nbrahman.eventsusinglistener;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Switch mySwitch;
    private TextView myTextView;
    private ListView myListView;
    String[] arrNum={"1  101", "2  201", "3  301", "4  401", "5  501", "6  601", "7  701", "8  801", "9  901", "10  1001", "11  1101", "12  1201", "13  1301", "14  1401", "15  1501", "16  1601", "17  1701", "18  1801", "19  1901", "20  2001", "21  2101"};
    public ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        myTextView = (TextView)findViewById(R.id.textView);
        myListView = (ListView)findViewById(R.id.lstTest);

        adapter = new ArrayAdapter<String>(this, R.layout.lstviewlayout,arrNum);
        myListView.setAdapter(adapter);

        mySwitch = (Switch)findViewById (R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    myListView.setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.textView).setVisibility(View.INVISIBLE);
                    myListView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}