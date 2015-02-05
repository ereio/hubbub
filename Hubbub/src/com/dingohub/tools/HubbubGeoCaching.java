package com.dingohub.tools;

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

public class HubbubGeoCaching implements 
LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	
	public static final long ONE_MIN = 1000 * 60;
	public static final long HALF_MIN = 1000 * 30;
	public static final long REFRESH_TIME = ONE_MIN * 5;
	public static final float MIN_ACCURACY = 500.0f;
	public static final String TAG = "HubbubGeocaching";
	/// Vars
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    Activity locationActivity;
    
    ////// Function Block //////
	public HubbubGeoCaching(Activity activity) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		locationRequest.setInterval(ONE_MIN);
		locationRequest.setFastestInterval(HALF_MIN);
		locationActivity = activity;
		
		googleApiClient = new GoogleApiClient.Builder(activity)
					.addApi(LocationServices.API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
		
		if(googleApiClient != null){
			googleApiClient.connect();
		}
	}
    
    
    
    @Override
	public void onLocationChanged(Location location) {
		if(null == this.location || location.getAccuracy() < this.location.getAccuracy()){
			this.location = location;
			
			if(this.location.getAccuracy() < MIN_ACCURACY){
				fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
			}
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
		if(currentLocation != null && currentLocation.getTime() > REFRESH_TIME){
			location = currentLocation;
		} else {
			fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // Schedule a Thread to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    fusedLocationProviderApi.removeLocationUpdates(googleApiClient,
                    		HubbubGeoCaching.this);
                }
            }, ONE_MIN, TimeUnit.MILLISECONDS);
		}
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Location getLocation(){
		return this.location;
	}

	
    public boolean googlePlayServicesAvailable(){
    	int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(locationActivity);
    	if(ConnectionResult.SUCCESS == status)
    		return true;
    	else{
    		GooglePlayServicesUtil.getErrorDialog(status, locationActivity, 0).show();
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
				
				String locality = new String(address.getLocality() + "_" + address.getAdminArea());
				return locality;
			} else {
				return "No Address Found";
			}
		}
	}
	/*
	///////////////Service Implemenations///////////////////
    @Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
    
    	*/



}
