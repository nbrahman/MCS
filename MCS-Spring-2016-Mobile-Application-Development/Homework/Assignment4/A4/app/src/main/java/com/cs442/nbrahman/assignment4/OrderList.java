package com.cs442.nbrahman.assignment4;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

class OrderMasterCustomAdapter extends ArrayAdapter<OrderMaster> {
	ArrayList<OrderedItem> arrlstOrderedItems = new ArrayList<> ();
	ListView lvOrderedItems;
	OrderedItemCustomAdapter ordItemsCustomeAdapter;

	ArrayList<OrderMaster> arrlstOrderMasterItems;
	ListView lstOrderMaster;
	Activity activity;
	private static LayoutInflater inflater=null;

	public OrderMasterCustomAdapter(Activity actSelected, int resource, ArrayList<OrderMaster> arrlstOrderMasterItems, ListView lstOrderMaster) {
		super(actSelected, resource);
		this.lstOrderMaster = lstOrderMaster;
		this.arrlstOrderMasterItems=arrlstOrderMasterItems;
		activity=actSelected;
		inflater = (LayoutInflater) activity.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return arrlstOrderMasterItems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class Holder
	{
		TextView txtOrderID;
		TextView txtDateTime;
		TextView txtTotal;
		TextView txtDiscount;
		TextView txtTaxes;
		TextView txtFinalOrderValue;
		//ListView lvOrderedItems;
		TextView txtOrderedItems;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder=new Holder();
		final View rowView;
		final OrderMaster curOrderItem = arrlstOrderMasterItems.get(position);

		rowView = inflater.inflate(R.layout.activity_ind_order, parent, false);
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
		holder.txtOrderID=(TextView) rowView.findViewById(R.id.txtOrderID);
		holder.txtDateTime=(TextView) rowView.findViewById(R.id.txtDateTime);
		holder.txtDateTime.setTypeface (Typeface.MONOSPACE);
		holder.txtDateTime.setTextSize (12);
		TextView lblDateTime = (TextView)rowView.findViewById (R.id.lblDateTime);
		lblDateTime.setTypeface (Typeface.MONOSPACE);
		lblDateTime.setTextSize (12);

		holder.txtTotal=(TextView) rowView.findViewById(R.id.txtTotal);
		holder.txtDiscount=(TextView) rowView.findViewById(R.id.txtDiscount);
		holder.txtTaxes=(TextView) rowView.findViewById(R.id.txtTaxes);
		holder.txtFinalOrderValue=(TextView) rowView.findViewById(R.id.txtFinalOrderValue);

		holder.txtOrderID.setText(String.valueOf (curOrderItem.intOrderID));
		holder.txtDateTime.setText (curOrderItem.strOrderTimestamp);
		holder.txtTotal.setText ("$" + new DecimalFormat ("#0.00").format (curOrderItem.dblOrderValue));
		holder.txtDiscount.setText ("$" + new DecimalFormat ("#0.00").format (curOrderItem.dblOrderDiscountVal));
		holder.txtTaxes.setText ("$" + new DecimalFormat ("#0.00").format (curOrderItem.dblOrderTaxes));
		holder.txtFinalOrderValue.setText ("$" + new DecimalFormat ("#0.00").format (curOrderItem.dblOrderValueFinal));


		OrderHelper ordHelper = new OrderHelper (super.getContext (), "MAD_Order", 3);

		arrlstOrderedItems = ordHelper.getOrderDetails (curOrderItem.intOrderID);

		if (arrlstOrderedItems != null)
		{
			/*ordItemsCustomeAdapter = new OrderedItemCustomAdapter (this.activity, R.layout.layout_ind_ordered_item, arrlstOrderedItems, holder.lvOrderedItems);
			holder.lvOrderedItems=(ListView) rowView.findViewById (R.id.lvOrderedItems);
			holder.lvOrderedItems.setAdapter (ordItemsCustomeAdapter);*/

			//holder.lvOrderedItems.setEnabled (false);
			String strOrderedItems = "";
			for (int i=0;i<arrlstOrderedItems.size ();i++)
			{
				if (i==0)
				{
					strOrderedItems = strOrderedItems + "\nItems Ordered\n"
							+ String.format("%2s", "#") + " "
							+ "Name" + String.format("%14s", " ") + " "
							+ String.format("%7s", "Price") + " "
							+ String.format("%7s", "Qty") + " "
							+ String.format("%7s", "Amount\n");
				}

				strOrderedItems = strOrderedItems + String.format("%2s", arrlstOrderedItems.get(i).intSrNo) + " "
						+ arrlstOrderedItems.get(i).strName + String.format("%" + String.valueOf(18 - arrlstOrderedItems.get(i).strName.length ()) + "s", " ") + " "
						+ String.format("%7s", new DecimalFormat ("#0.00").format (arrlstOrderedItems.get(i).dblPrice)) + " "
						+ String.format("%7s", new DecimalFormat ("#0.00").format (arrlstOrderedItems.get(i).dblQty)) + " "
						+ String.format("%7s", new DecimalFormat ("#0.00").format (arrlstOrderedItems.get(i).dblAmount)) + " " + "\n";
			}
			holder.txtOrderedItems=(TextView) rowView.findViewById(R.id.txtOrderedItems);
			holder.txtOrderedItems.setTypeface (Typeface.MONOSPACE);
			holder.txtOrderedItems.setTextSize (12);
			holder.txtOrderedItems.setText (strOrderedItems);
		}
		else
		{

		}

		return rowView;
	}
}

class OrderedItemCustomAdapter extends ArrayAdapter<OrderedItem> {
	ArrayList<OrderedItem> arrlstOrderedItems;
	ListView lstOrderedItems;
	Activity activity;
	private static LayoutInflater inflater=null;
	public OrderedItemCustomAdapter(Activity actSelected, int resource, ArrayList<OrderedItem> arrlstOrderedItems, ListView lstOrderedItems) {
		super(actSelected, resource);
		this.lstOrderedItems = lstOrderedItems;
		this.arrlstOrderedItems=arrlstOrderedItems;
		activity=actSelected;
		inflater = (LayoutInflater) activity.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return arrlstOrderedItems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class HolderOrderedItem
	{
		TextView lblSrNo;
		TextView lblName;
		TextView lblPrice;
		TextView lblQty;
		TextView lblAmount;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HolderOrderedItem holderOrderedItem=new HolderOrderedItem();
		final View rowView;
		final OrderedItem curOrderedItem = arrlstOrderedItems.get(position);
		rowView = inflater.inflate(R.layout.layout_ind_ordered_item, null);
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
		holderOrderedItem.lblSrNo=(TextView) rowView.findViewById(R.id.lblSrNo);
		holderOrderedItem.lblName=(TextView) rowView.findViewById(R.id.lblName);
		holderOrderedItem.lblPrice=(TextView) rowView.findViewById(R.id.lblPricePerUnit);
		holderOrderedItem.lblQty=(TextView) rowView.findViewById(R.id.lblQty);
		holderOrderedItem.lblAmount=(TextView) rowView.findViewById(R.id.lblAmount);

		holderOrderedItem.lblSrNo.setText(String.valueOf (curOrderedItem.intSrNo));
		holderOrderedItem.lblName.setText (curOrderedItem.strName);
		holderOrderedItem.lblPrice.setText ("$" + new DecimalFormat ("#0.00").format (curOrderedItem.dblPrice));
		holderOrderedItem.lblQty.setText ("$" + new DecimalFormat ("#0.00").format (curOrderedItem.dblQty));
		holderOrderedItem.lblAmount.setText ("$" + new DecimalFormat ("#0.00").format (curOrderedItem.dblAmount));

		return rowView;
	}
}

public class OrderList extends AppCompatActivity {

	ArrayList<OrderMaster> arrlstOrders = new ArrayList<> ();
	ListView lvOrderMaster;
	OrderMasterCustomAdapter ordMasterCustomAdapter;
	Activity context = this;

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

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		OrderHelper ordHelper = new OrderHelper (this, "MAD_Order", 3);

		arrlstOrders = ordHelper.getOrders ();

		if ((arrlstOrders != null) && (arrlstOrders.size()!=0))
		{
			setContentView (R.layout.activity_order_list);

			getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

			lvOrderMaster=(ListView) findViewById(R.id.lvOrdList);
			ordMasterCustomAdapter = new OrderMasterCustomAdapter (this, R.layout.activity_ind_order, arrlstOrders, lvOrderMaster);
			lvOrderMaster.setAdapter (ordMasterCustomAdapter);

			//lvOrderMaster.setEnabled (false);

			Button btnBack = (Button)findViewById (R.id.btnBack);
			btnBack.setOnClickListener (new View.OnClickListener () {
				@Override
				public void onClick (View v) {
					onBackPressed ();
				}
			});
		}
		else
		{
			Toast.makeText (this.getApplicationContext (), "No orders placed!!",Toast.LENGTH_LONG).show();
		}
	}
}

//class for Database Helper
class OrderHelper extends SQLiteOpenHelper {
	public final String DATABASE_NAME;

	public OrderHelper (Context context, String strDBName, int intDatabaseVersion) {
		super (context, strDBName, null, intDatabaseVersion);
		this.DATABASE_NAME = strDBName;
	}

	@Override
	public void onCreate (SQLiteDatabase db)
	{
		//boolean flgDBExists = checkDataBase (DATABASE_NAME);

		//db.execSQL("DROP TABLE IF EXISTS order_master");
		//db.execSQL("DROP TABLE IF EXISTS order_details");
		//if (!flgDBExists) {
			db.execSQL (
					"create table order_master " +
							"(order_id integer primary key, order_timestamp DATETIME DEFAULT (datetime('now','localtime')), order_value real, " +
							"order_discount_perc real, order_discount_val real, order_taxes real, order_value_final real)"
			db.execSQL (
					"create table order_details " +
							"(order_id integer, item_sr_no integer, item_name text, item_price real, " +
							"item_qty real, item_amount real, primary key (order_id, item_sr_no))"
			);
			);
			//db.close();
		//}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS order_master");
		db.execSQL("DROP TABLE IF EXISTS order_details");
		onCreate (db);
	}

	//function to check if database exists or not starts from here
	private boolean checkDataBase(String strDBName)
	{
		SQLiteDatabase checkDB = null;
		try
		{
			checkDB = SQLiteDatabase.openDatabase(strDBName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException e)
		{
			//database does't exist yet.
		}

		if(checkDB != null)
		{
			checkDB.close();
		}
		return checkDB != null;
	}
	//function to check if database exists or not ends over here

	//function to insert complete Order details starts from here
	public int insertOrder (ArrayList <SelMenuItem> arrlstSelMenuItems, double dblOrderValue,
							double dblOrderDiscountPerc, double dblOrderDiscountVal,
							double dblOrderTaxes, double dblOrderValueFinal)
	{
		String strOrderTimestamp;
		int intOrderID;
		String countQuery = "SELECT IFNULL (MAX (IFNULL (ORDER_ID, 0)), 0) + 1 ORDERID FROM ORDER_MASTER";
		SQLiteDatabase db = this.getWritableDatabase ();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor != null) {
			cursor.moveToFirst ();
			intOrderID = cursor.getInt (0);
		}
		else
		{
			intOrderID = -1;
		}
		cursor.close();
		db.close();

		if (intOrderID != 0) {
			String strTimeStamp = "SELECT STRFTIME('%m-%d-%Y %H:%M', DATETIME ('NOW', 'LOCALTIME'));";
			db = this.getReadableDatabase();
			cursor = db.rawQuery (strTimeStamp, null);
			if (cursor != null) {
				cursor.moveToFirst ();
				strOrderTimestamp = cursor.getString (0);
			}
			else
			{
				strOrderTimestamp = "";
			}
			cursor.close();
			db.close();
			ContentValues contentValues = new ContentValues ();
			contentValues.put ("order_id", intOrderID);
			//contentValues.put ("order_timestamp", "DATETIME ('NOW', 'LOCALTIME')");
			contentValues.put ("order_value", dblOrderValue);
			contentValues.put ("order_discount_perc", dblOrderDiscountPerc);
			contentValues.put ("order_discount_val", dblOrderDiscountVal);
			contentValues.put ("order_taxes", dblOrderTaxes);
			contentValues.put ("order_value_final", dblOrderValueFinal);

			db = this.getWritableDatabase ();
			db.beginTransaction ();
			try {
				db.insert ("order_master", null, contentValues);
			}
			catch (Exception e) {
				//Log.d ("Assign 4 - InsOrdMast", e.getMessage ());
				intOrderID = -1;
				db.endTransaction ();
				db.close ();
				return intOrderID;
			}
			for (int i = 0; i < arrlstSelMenuItems.size (); i++) {
				contentValues = new ContentValues ();
				contentValues.put ("order_id", intOrderID);
				contentValues.put ("item_sr_no", i + 1);
				contentValues.put ("item_name", arrlstSelMenuItems.get (i).strName);
				contentValues.put ("item_price", arrlstSelMenuItems.get (i).dblPrice);
				contentValues.put ("item_qty", arrlstSelMenuItems.get (i).dblQty);
				contentValues.put ("item_amount", arrlstSelMenuItems.get (i).dblAmount);

				try {
					db = this.getWritableDatabase ();
					db.insert ("order_details", null, contentValues);
				}
				catch (Exception e) {
					//Log.d ("Assign 4 - InsOrdDet", e.getMessage ());
					intOrderID = -1;
					db.endTransaction ();
					db.close();
					return intOrderID;
				}
			}
			db.setTransactionSuccessful ();
			db.endTransaction ();
			db.close ();
			return intOrderID;
		}
		else
		{
			db.close ();
			return -1;
		}
	}
	//function to insert complete Order details ends over here

	//function to get Order details starts from here
	public ArrayList<OrderMaster> getOrders ()
	{
		ArrayList<OrderMaster>  arrlstRetVal = new ArrayList<OrderMaster> ();
		String countQuery = "SELECT order_id, STRFTIME('%m-%d-%Y %H:%M', order_timestamp) order_timestamp, order_value, " +
				"order_discount_perc, order_discount_val, order_taxes, order_value_final FROM ORDER_MASTER ORDER BY ORDER_ID DESC";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor != null)
		{
			cursor.moveToFirst ();
			while (cursor.isAfterLast ()==false)
			{
				OrderMaster objCurOrdMaster = new OrderMaster ();
				objCurOrdMaster.intOrderID = cursor.getInt (0);
				objCurOrdMaster.strOrderTimestamp = cursor.getString (1);
				objCurOrdMaster.dblOrderValue = cursor.getDouble (2);
				objCurOrdMaster.dblOrderDiscountPerc = cursor.getDouble (3);
				objCurOrdMaster.dblOrderDiscountVal = cursor.getDouble (4);
				objCurOrdMaster.dblOrderTaxes = cursor.getDouble (5);
				objCurOrdMaster.dblOrderValueFinal = cursor.getDouble (6);
				arrlstRetVal.add (objCurOrdMaster);
				cursor.moveToNext ();
			}
			db.close ();
			return arrlstRetVal;
		}
		else
		{
			db.close ();
			return null;
		}
	}
	//function to get Order details ends over here

	//function to get Ordered Item details starts from here
	public ArrayList<OrderedItem> getOrderDetails (int intOrderID)
	{
		String countQuery = "SELECT IFNULL (ITEM_SR_NO, 0) ITEM_SR_NO, ITEM_NAME, ITEM_PRICE, ITEM_QTY, ITEM_AMOUNT FROM ORDER_DETAILS WHERE ORDER_ID=" + intOrderID + " ORDER BY ITEM_SR_NO";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor != null) {
			ArrayList<OrderedItem> arrlstOrderedItems = new ArrayList<OrderedItem> ();
			cursor.moveToFirst ();
			while(cursor.isAfterLast() == false) {
				OrderedItem curMenuItem = new OrderedItem ();
				curMenuItem.intSrNo = cursor.getInt (0);
				curMenuItem.strName = cursor.getString (1);
				curMenuItem.dblPrice = cursor.getDouble (2);
				curMenuItem.dblQty = cursor.getDouble (3);
				curMenuItem.dblAmount = cursor.getDouble (4);
				arrlstOrderedItems.add (curMenuItem);
				cursor.moveToNext ();
			}
			db.close ();
			return arrlstOrderedItems;
		}
		else
		{
			db.close ();
			return null;
		}
	}
	//function to get Ordered Item details ends over here
}

class OrderMaster implements Serializable
{
	int intOrderID;
	String strOrderTimestamp;
	double dblOrderValue;
	double dblOrderDiscountPerc;
	double dblOrderDiscountVal;
	double dblOrderTaxes;
	double dblOrderValueFinal;
}

class OrderedItem implements Serializable {
	int intSrNo;
	String strName="";
	double dblPrice;
	double dblQty=0;
	double dblAmount=0;
}