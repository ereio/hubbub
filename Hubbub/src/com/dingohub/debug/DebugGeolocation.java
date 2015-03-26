package com.dingohub.debug;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dingohub.base_activities.BaseGoogleActivity;
import com.dingohub.hubbub.R;

public class DebugGeolocation extends BaseGoogleActivity{
	private static final String TAG = "DEBUG_GEO";
	
	Button btnConnect;
	Button btnFindAddress;
	TextView tConnected;
	TextView tAddress;
	BaseGoogleActivity geoCache;
	Location location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_geolocation_activity);
		
		btnConnect = (Button) findViewById(R.id.button_connect);
		btnFindAddress = (Button) findViewById(R.id.button_findAddress);
		tConnected = (TextView) findViewById(R.id.info_connected);
		tAddress = (TextView) findViewById(R.id.info_address);
		
		btnConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		btnFindAddress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tAddress.setText(geoCache.getLocality());
			}
		});
    
    		
    	
    }
}

