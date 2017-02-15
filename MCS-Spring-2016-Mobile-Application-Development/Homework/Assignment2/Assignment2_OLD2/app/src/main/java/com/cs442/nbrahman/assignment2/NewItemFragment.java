package com.cs442.nbrahman.assignment2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Niks on 2/13/2016.
 */
public class NewItemFragment extends Fragment
{
	private OnNewItemAddedListener onNewItemAddedListener;

	private int cnt = 0;
	public interface OnNewItemAddedListener
	{
		public void onNewItemAdded(String strNewName);
		public int getCount();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.newitem_layout, container, false);
		//     cnt = 0;
		final EditText myEditTextName = (EditText)view.findViewById(R.id.myEditTextName);
		final EditText myEditTextDesc = (EditText)view.findViewById(R.id.myEditTextDesc);
		final EditText myEditTextPrice = (EditText)view.findViewById(R.id.myEditTextPrice);

		myEditTextName.setOnKeyListener
				(
						new View.OnKeyListener()
						{
							public boolean onKey(View v, int keyCode, KeyEvent event)
							{
								if (event.getAction() == KeyEvent.ACTION_DOWN)
								{
									if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) ||
											(keyCode == KeyEvent.KEYCODE_ENTER))
									{
										String strNewName = myEditTextName.getText ().toString ();
										String strNewDesc = myEditTextDesc.getText ().toString ();
										String strNewPrice = myEditTextPrice.getText ().toString ();
										//  String strNewName = strNewName1.replaceAll("\\s|\\r|\\n", "");
										strNewName = strNewName.trim ();
										strNewDesc = strNewDesc.trim ();
										strNewPrice = strNewPrice.trim ();
										Log.d ("DEBug:", "Key pressed.");
										if (strNewName.isEmpty ())
										{
											myEditTextName.setText ("");
											return true;
										}
										if (strNewDesc.isEmpty ())
										{
											myEditTextDesc.setText ("");
											return true;
										}
										if (strNewPrice.isEmpty ())
										{
											myEditTextPrice.setText ("");
											return true;
										}
										if (cnt == 0)
										{
											cnt = onNewItemAddedListener.getCount ();
										}
										cnt++;
										StringBuilder aStr = new StringBuilder ();
										aStr.append ("");
										aStr.append (cnt);
										aStr.append (".    ");
										aStr.append (strNewName);
										aStr.append ("    ");
										aStr.append (strNewDesc);
										aStr.append ("    ");
										aStr.append (strNewPrice);

										String newItem = aStr.toString ();
										onNewItemAddedListener.onNewItemAdded (newItem);
										myEditTextName.setText ("");
										myEditTextDesc.setText ("");
										myEditTextPrice.setText ("");
										return true;
									}
								}
								return false;
							}
						}
				);
		return view;
	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			onNewItemAddedListener = (OnNewItemAddedListener)activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() +
					" must implement OnNewItemAddedListener");
		}
	}
}
