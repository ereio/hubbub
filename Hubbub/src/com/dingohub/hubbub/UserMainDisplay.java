package com.dingohub.hubbub;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dingohub.activities_user.CreateEventsActivity;
import com.dingohub.activities_user.SearchEventsActivity;
import com.dingohub.base_activities.BaseGoogleActivity;
import com.dingohub.debug.DebugGeolocation;
import com.dingohub.fragments_user.TodaysBubsFragment;
import com.dingohub.fragments_user.UserBubsFragment;
import com.dingohub.fragments_user.UserGroupsFragment;
import com.dingohub.fragments_user.UserHubsFragment;
import com.dingohub.fragments_user.UserProfileFragment;
import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hub_database.HubUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseUser;

@SuppressWarnings("deprecation")
public class UserMainDisplay extends BaseGoogleActivity {
	
	public final static String TAG = "UserMainDisplay";

	// Keys for shared preferences strings
	public final static String USER_KEY = "UserKey";
	public final static String PASS_KEY = "PassKey";
	public final static String USER_LOC = "CurrentLocation";
	
	// Actual Shared Preferences File
	public final static String USER_SETTINGS = "UserSettings";
	
	// Tabs
	public final static String TAB_TODAY = "Today";
	public final static String TAB_HUBS = "Hubs";
	public final static String TAB_BUBS = "Bubs";
	public final static String TAB_GROUPS = "Groups";
	public static String [] TABS;
	
	// Current User Data
	HubUser user;
	SharedPreferences settings;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// init user
		user = HubDatabase.getCurrentUser();
		
		// Checks if activity reached without user data
		if(user == null){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		// Initialize Pager to view separate fragments and ActionBarTabs (deprecated)
		TABS = new String[] {TAB_TODAY, TAB_HUBS, TAB_BUBS, TAB_GROUPS,  user.username};
		init_pager_tabs();
		
	}

	
	@Override
	public void onConnected(Bundle arg0) {
		super.onConnected(arg0);
		Toast.makeText(getApplicationContext(), "TESTING HIT", Toast.LENGTH_LONG).show();
		
		String locality = null;
		while(locality == null){
			locality = this.getLocality();
			Log.e(TAG, "Searching..." + locality);
		}
		Log.e(TAG, "Locality found = " + locality);
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		

		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//Intent intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(intent);
			//return true;
		}
		if (id == R.id.search_bub) {
			Intent i = new Intent(this,SearchEventsActivity.class);
			startActivity(i);
			return true;
		}
		if (id == R.id.create_bub) {
			Intent i = new Intent(this,CreateEventsActivity.class);
			startActivity(i);
			return true;
		}
		else if(id == R.id.logout){
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			SharedPreferences settings = getSharedPreferences(LoginActivity.LOGIN_SETTINGS, 0);
			SharedPreferences.Editor editSettings = settings.edit();
			editSettings.putBoolean(LoginActivity.AUTO_LOG, false);
			editSettings.putString(USER_KEY, null);
			editSettings.putString(PASS_KEY, null);
			editSettings.apply();
			intent.putExtra(LoginActivity.LOGOUT_DEFAULT, true);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		else if (id == R.id.DEBUG_GEO){
			Intent i = new Intent(this, DebugGeolocation.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init_pager_tabs(){
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new HubActionBarListener();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// Set listener and what to do onPageSelected
		mViewPager.setOnPageChangeListener(new HubPagerListener());

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			switch(position){
			case 0:
				return new TodaysBubsFragment();
			case 1:
				return new UserHubsFragment();
			case 2:
				return new UserBubsFragment();
			case 3:
				return new UserGroupsFragment();
			case 4:
				return new UserProfileFragment();
			default:
				return new Fragment();
			}
		}

		@Override
		public int getCount() {
			return 5;
		}

		// Implemented to modulate pages separate from current Tab implementation
		// Precaution against ActionBarTab Deprecation
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return TABS[0];
			case 1:
				return TABS[1];
			case 2:
				return TABS[2];
			case 3:
				return TABS[3];
			case 4:
				return TABS[4];
			default:
				return null;
			}
		}
	}

	// WILL BE DELETED - SCHEDULED DEPRECATION
	public class HubActionBarListener implements ActionBar.TabListener{

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/*
	 * ----- SectionPageTabListener
	 * Updates Tabs representation of current fragment
	 * Though suppressing current deprecation, can be reused
	 * Only needs to update new Tab functionality when ActionBarTabs are removed
	 * */
	public class HubPagerListener implements ViewPager.OnPageChangeListener{

		
		@Override
		public void onPageScrollStateChanged(int position) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageSelected(int position) {
			getActionBar().setSelectedNavigationItem(position);
			
		}
		
	}



    
}
