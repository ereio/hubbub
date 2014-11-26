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
import android.view.Menu;
import android.view.MenuItem;

import com.dingohub.activities_user.CreateEventsActivity;
import com.dingohub.fragments_user.TodaysBubsFragment;
import com.dingohub.fragments_user.UserBubsFragment;
import com.dingohub.fragments_user.UserGroupsFragment;
import com.dingohub.fragments_user.UserHubsFragment;
import com.dingohub.fragments_user.UserProfileFragment;
import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hub_database.HubUser;

@SuppressWarnings("deprecation")
public class UserMainDisplay extends Activity {
	public final static String USER_KEY = "UserKey";
	public final static String PASS_KEY = "PassKey";
	public final static String USER_SETTINGS = "UserSettings";
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
		user = HubDatabase.getCurrentUser();
		
		// If user no current user, serious security breach!
		if(user == null){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		// Initialize Pager to view separate fragments and ActionBarTabs (deprecated)
		TABS = new String[] {TAB_TODAY, TAB_HUBS, TAB_BUBS, TAB_GROUPS,  user.username};
		init_pager_tabs();

		// Setting up Auto Log in to previously logged in account
		settings = getSharedPreferences(LoginActivity.LOGIN_SETTINGS, 0);
		SharedPreferences.Editor editAuto = settings.edit();
		editAuto.putBoolean(LoginActivity.AUTO_LOG, true);
		editAuto.commit();
		
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
			Intent i = new Intent(this,CreateEventsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				return new UserBubsFragment();
			case 2:
				return new UserHubsFragment();
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
