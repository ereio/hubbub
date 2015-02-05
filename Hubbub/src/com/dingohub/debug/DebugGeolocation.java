package com.dingohub.debug;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.hubbub.R;
import com.dingohub.tools.HubbubGeoCaching;
import com.google.android.gms.location.LocationListener;

public class DebugGeolocation extends Activity{
	private static final String TAG = "DEBUG_GEO";
	
	Button btnConnect;
	Button btnFindAddress;
	TextView tConnected;
	TextView tAddress;
	HubbubGeoCaching geoCache;
	Location location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_geolocation_activity);
		
		geoCache = new HubbubGeoCaching(this);
		
		btnConnect = (Button) findViewById(R.id.button_connect);
		btnFindAddress = (Button) findViewById(R.id.button_findAddress);
		tConnected = (TextView) findViewById(R.id.info_connected);
		tAddress = (TextView) findViewById(R.id.info_address);
		
		btnConnect.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String locationResult = new String();
				location = geoCache.getLocation();
				
				if(location != null){
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					float accuracy = location.getAccuracy();
					double altitude = location.getAltitude();
					// Need to account for 17 > version that can't use getElapsedRealTimenanaos
					double elapsedTimeSecs = (double) location.getElapsedRealtimeNanos() / 1000000000.0;
					locationResult = "Latitude: " + latitude + "\n" +
	                        "Longitude: " + longitude + "\n" +
	                        "Altitude: " + altitude + "\n" +
	                        "Accuracy: " + accuracy + "\n" +
	                        "Elapsed Time: " + elapsedTimeSecs + " secs" + "\n";
				} else {
					locationResult = "Location Not Available!";
				}
				tConnected.setText(locationResult);
			}
		});
		
		btnFindAddress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tAddress.setText(geoCache.getLocality());
			}
		});
    
    		
    	
    }
}

