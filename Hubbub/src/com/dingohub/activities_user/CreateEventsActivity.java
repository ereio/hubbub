package com.dingohub.activities_user;

import com.dingohub.hubbub.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateEventsActivity extends Activity{
	private String [] tap_time;
	private Spinner pingIn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_bub);
		
		 tap_time = new String[]{"0 hours before", "1 hour before","2 hours before","3 hours before",
				"4 hours before","5 hours before","6 hours before","7 hours before","8 hours before"};
		 
		 pingIn = (Spinner) findViewById(R.id.pingIn_spinner);
		 
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item ,tap_time);
		 pingIn.setAdapter(adapter);
		 
	}
}
