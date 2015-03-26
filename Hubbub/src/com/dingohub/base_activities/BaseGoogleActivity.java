package com.dingohub.base_activities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class BaseGoogleActivity extends Activity implements 
LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = "BaseGoogleActivity";
	
	public static final long ONE_MIN = 1000 * 60;
	public static final long HALF_MIN = 1000 * 30;
	public static final long REFRESH_TIME = ONE_MIN * 5;
	public static final float MIN_ACCURACY = 500.0f;
	
	/// Vars
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private String locality;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    
    ////// Function Block //////
	public BaseGoogleActivity() {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		locationRequest.setInterval(ONE_MIN);
		locationRequest.setFastestInterval(HALF_MIN);
		
		if(googlePlayServicesAvailable()){
			googleApiClient = new GoogleApiClient.Builder(this)
						.addApi(LocationServices.API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();
		
			if(googleApiClient != null){
				googleApiClient.connect();
			}
		} else {
			Log.i(TAG, "GooglePlayServices not available");
		}
	}
    
	
    @Override
	public void onLocationChanged(Location location) {
    	Log.i(TAG, "Connection Changed");
		if(null == this.location || location.getAccuracy() < this.location.getAccuracy()){
			this.location = location;
			locality = new GetAddressTask(getApplicationContext()).doInBackground(location);
			if(this.location.getAccuracy() < MIN_ACCURACY){
				fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
			}
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i(TAG, "Connection Failed");
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "Connection Complete");
		Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
		if(currentLocation != null && currentLocation.getTime() > REFRESH_TIME){
			location = currentLocation;
			locality = new GetAddressTask(getApplicationContext()).doInBackground(location);
		} else {
			fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // Schedule a Thread to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    fusedLocationProviderApi.removeLocationUpdates(googleApiClient,
                    		BaseGoogleActivity.this);
                }
            }, ONE_MIN, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.i(TAG, "Connection Suspended");
		
	}

	public Location getLocation(){
		return this.location;
	}

	public String getLocality(){
		return this.locality;
	}
	
    public boolean googlePlayServicesAvailable(){
    	int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(ConnectionResult.SUCCESS == status)
    		return true;
    	else{
    		GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
    		finish();
    		return false;
    	}
    }
	
	public class GetAddressTask extends AsyncTask<Location, Void, String>{
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
				Log.i(TAG, addressText);
				
				locality = new String(address.getLocality() + "_" + address.getAdminArea());
				
				return locality;
			} else {
				return "No Address Found";
			}
		}
	}
}
