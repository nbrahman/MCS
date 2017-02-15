package com.cs442.nbrahman.Assignment4_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderList extends AppCompatActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_order_list);
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

		//db.execSQL("DROP TABLE order_master");
		//db.execSQL("DROP TABLE order_details");
		//if (!flgDBExists) {
		db.execSQL (
				"create table if not exists order_master " +
						"(order_id integer primary key, order_timestamp text, order_value real, " +
						"order_discount_perc real, order_discount_val real, order_taxes real, order_value_final real)"
		);
		db.execSQL (
				"create table if not exists order_details " +
						"(order_id integer, item_sr_no integer, item_name text, item_price real, " +
						"item_qty real, item_amount real, primary key (order_id, item_sr_no))"
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
	public int insertOrder (ArrayList<SelMenuItem> arrlstSelMenuItems, double dblOrderValue,
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
			String strTimeStamp = "SELECT STRFTIME('%M-%D-%Y', DATE ('NOW', 'LOCALTIME')) || ' ' || STRFTIME('%H:%M', TIME ('NOW', 'LOCALTIME'))";
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
			contentValues.put ("order_timestamp", strOrderTimestamp);
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
				Log.d ("Assign 4 - InsOrdMast", e.getMessage ());
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
					Log.d ("Assign 4 - InsOrdDet", e.getMessage ());
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
		String countQuery = "SELECT * FROM ORDER_MASTER ORDER BY ORDER_ID";
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