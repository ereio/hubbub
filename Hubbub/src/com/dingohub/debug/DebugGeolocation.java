package com.dingohub.debug;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dingohub.hubbub.R;
import com.dingohub.tools.HubbubGeoCaching;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

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
		
		if(!googlePlayServicesAvailable())
			finish();
		
		geoCache = new HubbubGeoCaching(this);
		
		btnConnect = (Button) findViewById(R.id.button_connect);
		tConnected = (TextView) findViewById(R.id.info_connected);
		tAddress = (TextView) findViewById(R.id.info_address);
		
		btnConnect.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String locationResult = new String();
				Location location = geoCache.getLocation();
				
				if(location != null){
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					float accuracy = location.getAccuracy();
					double altitude = location.getAltitude();
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
				
				tAddress.setText(new GetAddressTask(getApplicationContext()).doInBackground(location));
			}
		});
		
		
	}
	
	private class GetAddressTask extends AsyncTask<Location, Void, String>{
		Context mContext;
		
		public GetAddressTask(Context context){
			super();
			mContext = context;
		}
		
		@Override
		protected String doInBackground(Location... locations) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Location location = locations[0];
			
			List<Address> addresses = null;
			try{
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			} catch (IOException e){
				Log.e(TAG, "geocoder hit IOException\n\n");
				e.printStackTrace();
			}
			
			if(addresses != null && addresses.size() > 0){
				Address address = addresses.get(0);
				String addressText = String.format("%s, %s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getLocality(),
						address.getAdminArea(),
						address.getCountryName());
				return addressText;
			} else {
				return "No Address Found";
			}
		}
	}
		

    public boolean googlePlayServicesAvailable(){
    	int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(ConnectionResult.SUCCESS == status)
    		return true;
    	else{
    		GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
    		return false;
    	}
    		
    	
    }
	
}
