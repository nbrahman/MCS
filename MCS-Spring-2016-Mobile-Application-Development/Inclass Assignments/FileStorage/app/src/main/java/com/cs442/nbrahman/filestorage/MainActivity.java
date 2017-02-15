package com.cs442.nbrahman.filestorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		Button btnAdd = (Button)findViewById (R.id.btnAdd);
		btnAdd.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				WriteToFile ();
			}
		});

	}


	public void WriteToFile () {
		EditText editText = (EditText)findViewById (R.id.editText);
		final String strCont = editText.getText ().toString ();
		try {
			File fis = new File (getFilesDir () + "/Test.txt");
			//if (fis.getParentFile ().mkdirs ())
			//{
				Log.d("File Storage", getFilesDir ().toString ());
				fis.createNewFile ();
				FileOutputStream fos = new FileOutputStream (fis);
				fos.write (strCont.getBytes ());
				fos.flush ();
				fos.close ();
				Toast.makeText (getBaseContext(), "File saved successfully to " + getFilesDir ().toString () + "/Test.txt", Toast.LENGTH_LONG).show ();
			//}
		}
		catch (Exception e)
		{
			Log.d ("File Storage Assignment", e.getMessage ().toString ());
		}
	}
}
