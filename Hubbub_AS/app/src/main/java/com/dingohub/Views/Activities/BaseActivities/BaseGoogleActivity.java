package com.dingohub.Views.Activities.BaseActivities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.SharedPrefKeys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class BaseGoogleActivity extends ActionBarActivity implements
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
    private Location location;
    private String locality;
    
    protected FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;   
    protected GoogleApiClient googleApiClient;
    protected GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
            // TODO - Implement a callback bool for highlighting
            // If a user as touched down on an object, it should be sent back with a
            // highlighting bool for layout objects to render a "pressed view"
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // this is to confirm a user wanted to actually select the object they tapped
                return true;
            }
        });

		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		locationRequest.setInterval(ONE_MIN);
		locationRequest.setFastestInterval(HALF_MIN);
		
		googleApiClient = new GoogleApiClient.Builder(this)
						.addApi(LocationServices.API)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.build();
		
		if(googleApiClient != null)
				googleApiClient.connect();
		else
		    Log.i(TAG, "GooglePlayServices not available");
		
	}
	
	
    @Override
	public void onLocationChanged(Location location) {
    	Log.i(TAG, "Connection Changed");
		if(null == this.location || location.getAccuracy() < this.location.getAccuracy()){
			this.location = location;
			locality = new GetAddressTask(getApplicationContext()).doInBackground(location);
			HubDatabase.SetLocation(locality);
			if(this.location.getAccuracy() < MIN_ACCURACY){
				fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
			}
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		GooglePlayServicesUtil.getErrorDialog(		
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, 0).show();
		Log.i(TAG, "Failed to connect to Google API client");
		finish();
		
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "Connection Complete");
		Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
		if(currentLocation != null && currentLocation.getTime() > REFRESH_TIME){
			location = currentLocation;
			locality = new GetAddressTask(getApplicationContext()).doInBackground(location);
			HubDatabase.SetLocation(locality);
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

                SharedPreferences settings = getSharedPreferences(SharedPrefKeys.LOGIN_SETTINGS, 0);
                settings.getString(SharedPrefKeys.LOC_KEY, getLocality());
				
				return locality;
			} else {
				return "No Address Found";
			}
		}
	}
}
