package com.cs442.nbrahman.assignment4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

class SelMenuCustomAdapter extends ArrayAdapter<MenuItem> {
	ArrayList<SelMenuItem> lstSelMenuItems;
	ListView lstSelMenu;
	Activity activity;
	private static LayoutInflater inflater=null;
	double dblTotalOrderValue=0;
	public SelMenuCustomAdapter(Activity actSelected, int resource, ArrayList<SelMenuItem> arrlstSelMenuItems, ListView lstSelMenu) {
		super(actSelected, resource);
		this.lstSelMenu = lstSelMenu;
		lstSelMenuItems=arrlstSelMenuItems;
		activity=actSelected;
		inflater = (LayoutInflater) activity.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return lstSelMenuItems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class Holder
	{
		ImageView img;
		TextView lblName;
		TextView lblPricePerUnit;
		EditText txtQty;
		TextView lblAmount;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder=new Holder();
		final View rowView;
		final SelMenuItem curMenuItem = lstSelMenuItems.get(position);
		rowView = inflater.inflate(R.layout.layout_sel_menu_list, null);
		if (position % 2 == 1) {
			rowView.setBackgroundColor(Color.parseColor ("#F3F3F1"));
		} else {
			rowView.setBackgroundColor(Color.WHITE);
		}
		if (position % 2 == 1) {
			rowView.setBackgroundColor(Color.parseColor ("#F3F3F1"));
		} else {
			rowView.setBackgroundColor(Color.WHITE);
		}
		holder.img=(ImageView) rowView.findViewById (R.id.imageView1);
		holder.lblName=(TextView) rowView.findViewById(R.id.lblName);
		holder.lblPricePerUnit=(TextView) rowView.findViewById(R.id.lblPricePerUnit);
		holder.lblAmount=(TextView) rowView.findViewById(R.id.lblAmount);
		holder.txtQty=(EditText) rowView.findViewById(R.id.txtQty);
		holder.lblName.setText(curMenuItem.strName);
		holder.img.setImageResource (curMenuItem.strImgName);
		holder.lblPricePerUnit.setText ("$" + new DecimalFormat ("#0.00").format (curMenuItem.dblPrice));
		holder.lblAmount.setText ("$" + new DecimalFormat ("#0.00").format (curMenuItem.dblPrice * curMenuItem.dblQty));
		holder.txtQty.setText (new DecimalFormat ("#0.00").format (curMenuItem.dblQty));

		holder.txtQty.addTextChangedListener (new TextWatcher () {
			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {
				lstSelMenuItems.get(position).dblQty = Double.parseDouble (s.toString ());
				lstSelMenuItems.get(position).dblAmount = lstSelMenuItems.get(position).dblPrice * lstSelMenuItems.get(position).dblQty;
				holder.lblAmount.setText ("$" + new DecimalFormat ("#0.00").format (lstSelMenuItems.get(position).dblAmount));
				dblTotalOrderValue = 0;
				for (int i = 0; i <lstSelMenuItems.size();i++)
				{
					dblTotalOrderValue += lstSelMenuItems.get (i).dblAmount;
				}

				EditText txtDiscountPercentage = (EditText) activity.findViewById (R.id.txtDiscountPercentage);
				double dblDiscountPerc=0;
				if ((txtDiscountPercentage.getText ().toString ()!="") && (txtDiscountPercentage.getText ().toString ()!=null) && (txtDiscountPercentage.getText ().toString ().isEmpty ()==false))
				{
					dblDiscountPerc = Double.parseDouble (txtDiscountPercentage.getText ().toString ());
				}
				else
				{
					dblDiscountPerc = 0;
				}
				double dblDiscountVal=0;
				dblDiscountVal = dblTotalOrderValue * (dblDiscountPerc / 100);

				TextView lblDiscountVal = (TextView) activity.findViewById (R.id.lblDiscountVal);
				lblDiscountVal.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblDiscountVal)));

				EditText txtTaxes = (EditText) activity.findViewById (R.id.txtTaxes);
				double dblTaxes=0;
				if ((txtTaxes.getText ().toString ()!="") && (txtTaxes.getText ().toString ()!=null) && (txtTaxes.getText ().toString ().isEmpty ()==false))
				{
					dblTaxes = Double.parseDouble (txtTaxes.getText ().toString ());
				}
				else
				{
					dblTaxes = 0;
				}
				//txtTaxes.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTaxes)));

				dblTotalOrderValue = dblTotalOrderValue - dblDiscountVal + dblTaxes;

				TextView lblTotalOrderValue = (TextView) activity.findViewById (R.id.lblTotalOrderValue);
				TextView valTotalOrderValue = (TextView) activity.findViewById (R.id.valTotalOrderValue);
				lblTotalOrderValue.setText ("Total Order Value: $");
				valTotalOrderValue.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTotalOrderValue)));
			}

			@Override
			public void afterTextChanged (Editable s) {
			}
		});
		/*rowView.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				Toast.makeText (context, curMenuItem.name + "\t\tPrice: $" + curMenuItem.price + "\n\n" + curMenuItem.description, Toast.LENGTH_LONG).show();
			}
		});

		rowView.setOnLongClickListener(new View.OnLongClickListener() {
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

public class OrderConfirmation extends AppCompatActivity {

	ArrayList<SelMenuItem> lstSelMenuItems = new ArrayList<SelMenuItem> ();
	ListView lvSelMenu;
	SelMenuCustomAdapter selMenuCustomAdapter;
	double dblTotalOrderValue=0;
	Activity context = this;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_order_confirmation);
		lstSelMenuItems.addAll ((ArrayList<SelMenuItem>) getIntent ().getSerializableExtra ("lstSelMenuItems"));

		getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

		lvSelMenu=(ListView) findViewById(R.id.lstSelItems);
		selMenuCustomAdapter = new SelMenuCustomAdapter(this, R.layout.layout_sel_menu_list, lstSelMenuItems, lvSelMenu);
		lvSelMenu.setAdapter (selMenuCustomAdapter);

		lvSelMenu.setEnabled (false);

		Button btnCancel = (Button)findViewById (R.id.btnCancel);
		Button btnConfirmOrder = (Button)findViewById (R.id.btnConfirmOrder);
		for (int i=0;i<lstSelMenuItems.size ();i++)
		{
			lstSelMenuItems.get(i).dblAmount = lstSelMenuItems.get(i).dblPrice * lstSelMenuItems.get(i).dblQty;
			dblTotalOrderValue += lstSelMenuItems.get (i).dblAmount;
		}

		EditText txtDiscountPercentage = (EditText) findViewById (R.id.txtDiscountPercentage);
		double dblDiscountPerc=0;
		if ((txtDiscountPercentage.getText ().toString ()!="") && (txtDiscountPercentage.getText ().toString ()!=null) && (txtDiscountPercentage.getText ().toString ().isEmpty ()==false))
		{
			dblDiscountPerc = Double.parseDouble (txtDiscountPercentage.getText ().toString ());
		}
		else
		{
			dblDiscountPerc = 0;
		}
		double dblDiscountVal=0;
		dblDiscountVal = dblTotalOrderValue * (dblDiscountPerc / 100);

		TextView lblDiscountVal = (TextView) findViewById (R.id.lblDiscountVal);
		lblDiscountVal.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblDiscountVal)));

		final EditText txtTaxes = (EditText) findViewById (R.id.txtTaxes);
		double dblTaxes=0;
		if ((txtTaxes.getText ().toString ()!="") && (txtTaxes.getText ().toString ()!=null) && (txtTaxes.getText ().toString ().isEmpty ()==false))
		{
			dblTaxes = Double.parseDouble (txtTaxes.getText ().toString ());
		}
		else
		{
			dblTaxes = 0;
		}
		//txtTaxes.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTaxes)));

		dblTotalOrderValue = dblTotalOrderValue - dblDiscountVal + dblTaxes;

		TextView lblTotalOrderValue = (TextView) findViewById (R.id.lblTotalOrderValue);
		TextView valTotalOrderValue = (TextView) findViewById (R.id.valTotalOrderValue);
		lblTotalOrderValue.setText ("Total Order Value: $");
		valTotalOrderValue.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTotalOrderValue)));

		btnCancel.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				onBackPressed ();
			}
		});

		btnConfirmOrder.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				new AlertDialog.Builder (context)
						.setIcon (android.R.drawable.ic_dialog_alert)
						.setMessage ("Are you sure you want to confirm this order?")
						.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialog, int which) {
								dblTotalOrderValue = 0;
								for (int i = 0; i < lstSelMenuItems.size (); i++) {
									lstSelMenuItems.get (i).dblAmount = lstSelMenuItems.get (i).dblPrice * lstSelMenuItems.get (i).dblQty;
									dblTotalOrderValue += lstSelMenuItems.get (i).dblAmount;
								}
								Double dblOrderValue = dblTotalOrderValue;

								EditText txtDiscountPercentage = (EditText) findViewById (R.id.txtDiscountPercentage);
								double dblDiscountPerc=0;
								if ((txtDiscountPercentage.getText ().toString ()!="") && (txtDiscountPercentage.getText ().toString ()!=null) && (txtDiscountPercentage.getText ().toString ().isEmpty ()==false))
								{
									dblDiscountPerc = Double.parseDouble (txtDiscountPercentage.getText ().toString ());
								}
								else
								{
									dblDiscountPerc = 0;
								}
								double dblDiscountVal=0;
								dblDiscountVal = dblTotalOrderValue * (dblDiscountPerc / 100);

								TextView lblDiscountVal = (TextView) findViewById (R.id.lblDiscountVal);
								lblDiscountVal.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblDiscountVal)));

								EditText txtTaxes = (EditText) findViewById (R.id.txtTaxes);
								double dblTaxes=0;
								if ((txtTaxes.getText ().toString ()!="") && (txtTaxes.getText ().toString ()!=null) && (txtTaxes.getText ().toString ().isEmpty ()==false))
								{
									dblTaxes = Double.parseDouble (txtTaxes.getText ().toString ());
								}
								else
								{
									dblTaxes = 0;
								}
								//txtTaxes.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTaxes)));

								dblTotalOrderValue = dblTotalOrderValue - dblDiscountVal + dblTaxes;

								TextView lblTotalOrderValue = (TextView) findViewById (R.id.lblTotalOrderValue);
								TextView valTotalOrderValue = (TextView) findViewById (R.id.valTotalOrderValue);
								lblTotalOrderValue.setText ("Total Order Value: $");
								valTotalOrderValue.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTotalOrderValue)));

								OrderHelper orderHelper = new OrderHelper (context, "MAD_Order", 3);
								int intStatus = orderHelper.insertOrder (lstSelMenuItems, dblOrderValue, dblDiscountPerc, dblDiscountVal, dblTaxes, dblTotalOrderValue);

								//Log.d ("Ass4: InsStatus", String.valueOf (intStatus));

								Intent intent = context.getIntent ();
								intent.putExtra ("TotalOrderValue", dblTotalOrderValue);
								context.setResult (RESULT_OK, intent);
								finish ();
							}
						})
						.setNegativeButton ("No", new DialogInterface.OnClickListener () {
							@Override
							public void onClick (DialogInterface dialog, int which) {
							}
						})
						.show ();
			}
		});

		txtDiscountPercentage.addTextChangedListener (new TextWatcher () {
			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {
				dblTotalOrderValue = 0;
				for (int i = 0; i < lstSelMenuItems.size (); i++) {
					lstSelMenuItems.get (i).dblAmount = lstSelMenuItems.get (i).dblPrice * lstSelMenuItems.get (i).dblQty;
					dblTotalOrderValue += lstSelMenuItems.get (i).dblAmount;
				}

				double dblDiscountVal = 0;
				double dblDiscountPerc = 0;
				String strDiscountPerc = "";
				strDiscountPerc = s.toString ();
				if ((strDiscountPerc != "") && (strDiscountPerc != null) && (s.toString ().isEmpty () == false)) {
					dblDiscountPerc = Double.parseDouble (strDiscountPerc);
				} else {
					dblDiscountPerc = 0;
					dblDiscountVal = 0;
				}
				dblDiscountVal = dblTotalOrderValue * (dblDiscountPerc / 100);
				TextView lblDiscountVal = (TextView) findViewById (R.id.lblDiscountVal);
				lblDiscountVal.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblDiscountVal)));

				EditText txtTaxes = (EditText) findViewById (R.id.txtTaxes);
				double dblTaxes = 0;
				if ((txtTaxes.getText ().toString () != "") && (txtTaxes.getText ().toString () != null) && (txtTaxes.getText ().toString ().isEmpty () == false)) {
					dblTaxes = Double.parseDouble (txtTaxes.getText ().toString ());
				} else {
					dblTaxes = 0;
				}
				//txtTaxes.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTaxes)));

				dblTotalOrderValue = dblTotalOrderValue - dblDiscountVal + dblTaxes;

				TextView lblTotalOrderValue = (TextView) findViewById (R.id.lblTotalOrderValue);
				TextView valTotalOrderValue = (TextView) findViewById (R.id.valTotalOrderValue);
				lblTotalOrderValue.setText ("Total Order Value: $");
				valTotalOrderValue.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTotalOrderValue)));
			}

			@Override
			public void afterTextChanged (Editable s) {
			}
		});

		txtTaxes.addTextChangedListener (new TextWatcher () {
			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {
				double dblDiscountVal=0;
				TextView txtDiscountVal = (TextView) findViewById (R.id.lblDiscountVal);
				if ((txtDiscountVal.getText ().toString ()!="") && (txtDiscountVal.getText ().toString ()!=null) && (txtDiscountVal.getText ().toString ().isEmpty ()==false))
				{
					dblDiscountVal = Double.parseDouble (txtDiscountVal.getText ().toString ());
				}
				else
				{
					dblDiscountVal = 0;
				}
				dblTotalOrderValue = 0;
				for (int i=0;i<lstSelMenuItems.size ();i++)
				{
					lstSelMenuItems.get(i).dblAmount = lstSelMenuItems.get(i).dblPrice * lstSelMenuItems.get(i).dblQty;
					dblTotalOrderValue += lstSelMenuItems.get (i).dblAmount;
				}
				double dblTaxes=0;
				if ((s.toString ()!="") && (s.toString ()!=null) && (s.toString ().isEmpty ()==false))
				{
					dblTaxes = Double.parseDouble (s.toString ());
				}
				else
				{
					dblTaxes = 0;
				}
				//txtTaxes.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTaxes)));

				dblTotalOrderValue = dblTotalOrderValue - dblDiscountVal + dblTaxes;

				TextView lblTotalOrderValue = (TextView) findViewById (R.id.lblTotalOrderValue);
				TextView valTotalOrderValue = (TextView) findViewById (R.id.valTotalOrderValue);
				lblTotalOrderValue.setText ("Total Order Value: $");
				valTotalOrderValue.setText (String.valueOf (new DecimalFormat ("#0.00").format (dblTotalOrderValue)));
			}

			@Override
			public void afterTextChanged (Editable s) {
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item)
	{
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				onBackPressed ();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}


class SelMenuItem implements Serializable {
	public int strImgName;
	public String strName;
	public String strDesc;
	public double dblPrice;
	public double dblQty=0;
	public double dblAmount=0;
}