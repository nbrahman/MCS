package com.nbrahman.iit.assignment2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


class CustomAdapter extends ArrayAdapter<FoodDetails> {
    ArrayList<FoodDetails> result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public CustomAdapter(MainActivity mainActivity, int resource, ArrayList<FoodDetails> prgmNameList) {
        // TODO Auto-generated constructor stub
        super(mainActivity, resource);
        result=prgmNameList;
        context=mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView name;
        TextView price;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        final View rowView;
        final FoodDetails item = result.get(position);
        rowView = inflater.inflate(R.layout.menu_list, null);
        holder.name=(TextView) rowView.findViewById(R.id.nameTextView);
        holder.price=(TextView) rowView.findViewById(R.id.priceTextView);
        holder.name.setText(item.name);
        holder.price.setText("$" + item.price);
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,  item.name + "\n\n" + item.description + "\n\nPrice: $" + item.price, Toast.LENGTH_LONG).show();
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to delete this Food item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) context).removeFoodItem(position);
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

    public static ArrayList<FoodDetails> prgmNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context=this;
        setTitle("Food Menu");

        lv=(ListView) findViewById(R.id.listView);
        prgmNameList = new ArrayList<FoodDetails>();

        populateListView();
        customAdaptor = new CustomAdapter(this, R.layout.menu_list, prgmNameList);
        lv.setAdapter(customAdaptor);

        View footerView = new View(context);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
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
                dialog.setTitle("Add Food Item To Menu");

                Button canclelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
                // if button is clicked, close the custom dialog
                canclelBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button addMenuBtn = (Button) dialog.findViewById(R.id.addMenuBtn);
                addMenuBtn.setOnClickListener(new OnClickListener() {
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
                            setItemInFoodDetails(name, description, price);
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
        String [] names = {"Bacon & Gouda Breakfast Sandwich","Classic Whole-Grain Oatmeal",
                           "Double-Smoked Bacon, Cheddar & Egg Sandwich", "Egg & Cheddar Breakfast Sandwich",
                           "Oatmeal with Fresh Blueberries"};

        String [] description = {"Sizzling Applewood smoked bacon, melty aged Gouda and a parmesan frittata are layered on an artisan roll for extra smoky breakfast goodness.",
                                 "A blend of rolled and steel-cut oats with dried fruit, a nut medley and brown sugar as optional toppings.",
                                 "Warm, fluffy egg and mild cheddar cheese are layered on a toasted organic wheat English muffin to create the classic taste of breakfast perfection.",
                                 "Thick-cut bacon is slow-smoked for 10 hours over hardwood, then stacked with a fluffy egg patty topped with creamy melted slice of sharp cheddar cheese on our signature croissant bun. It's double double delicious delicious.",
                                 "A blend of rolled and steel-cut oats with blueberries and agave syrup."};
        String [] price = {"5.00","4.67", "6.56", "4.49", "8.00"};

        for(int i=0; i<names.length;i++) {
            setItemInFoodDetails(names[i], description[i], price[i]);
        }
    }

    public void setItemInFoodDetails (String name, String description, String price) {
        FoodDetails item = new FoodDetails();
        item.name = name;
        item.description = description;
        item.price = price;

        prgmNameList.add(item);
    }

    public void removeFoodItem(int index) {
        prgmNameList.remove(index);
        customAdaptor.notifyDataSetChanged();
    }
}


class FoodDetails {
    public String name;
    public String description;
    public String price;
}