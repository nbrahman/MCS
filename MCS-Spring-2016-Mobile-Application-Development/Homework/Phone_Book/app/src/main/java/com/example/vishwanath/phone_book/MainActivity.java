package com.example.vishwanath.phone_book;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URI;

public class MainActivity extends ListActivity {

    ListView lv;
    Cursor cr;
    String selectedWord;
    long selectedWordId;
    @Override
    public long getSelectedItemId()
    {
        return super.getSelectedItemId();
    }
    @Override
    public int getSelectedItemPosition()
    {
        return super.getSelectedItemPosition();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String startsWith=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cr=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        String[] from={ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone._ID};
        if (startsWith != null && !startsWith.equals("")) {
            cr = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    from,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            + " like \"" + startsWith + "%\"", null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            + " ASC");
        } else {
            cr = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    from, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            + " ASC");
        }
        startManagingCursor(cr);

        int[] to={android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter listadapter=new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,cr,from,to){

            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.WHITE);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.WHITE);
                return view;

            };
        };
        setListAdapter(listadapter);
        lv=getListView();
       // lv=(ListView)findViewById(R.id.list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        registerForContextMenu(lv);
      /*  Button add=(Button)findViewById(R.id.add);
        add.setOnClickListener();*/

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
       /* android.net.Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        var cursor = activity.ContentResolver(uri, projection, null, null, null)*/;
    }

public void add(View view)
{
    Intent intent = new Intent(MainActivity.this, AddContact.class);
    startActivityForResult(intent, 1);

}
    public void delete(View view)
    {
        Intent intent = new Intent(MainActivity.this, DeleteContacts.class);
        startActivityForResult(intent, 1);
    }
@Override
    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
   /*AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;
    selectedWord = ((TextView) info.targetView).getText().toString();
    selectedWordId = info.id;*/
    Toast.makeText(this,selectedWord, Toast.LENGTH_LONG).show();
       // if (v.getId()=findViewById(R.id.)) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        //}
    }

    public boolean onContextItemSelected(final MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            Toast.makeText(this,"Edit function", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.delete) {
            Toast.makeText(this,"Edit function", Toast.LENGTH_LONG).show();
          /*  ContactHelper.deleteContact(getContentResolver(),
                    edtDeleteContactNumber.getText().toString());*/
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.d("onActivityResult", "inside");
        Log.d("requestcode", "value");

        if (requestCode == 1) {
            // Make sure the request was successful
            Log.d("requestcode","1");
            if (resultCode == RESULT_OK) {
                Log.d("resultok","1");
                try {




                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
