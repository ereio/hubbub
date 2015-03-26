package com.dingohub.fragments_user;

import java.util.ArrayList;
import java.util.Calendar;

import com.dingohub.activities_user.ViewEventActivity;
import com.dingohub.hub_database.Bub;
import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hubbub.R;
import com.dingohub.hubbub.UserMainDisplay;
import com.dingohub.tools.EventListAdapter;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


// note : make this fragment refresh ! without having to reopen the activity.
public class TodaysBubsFragment extends ListFragment{
	private static final int MONTH_OFFSET = 1;
	ArrayList<Bub> todays_events = null;
	EventListAdapter adapter;
	
	public TodaysBubsFragment() {}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_todays_bubs, container,
				false);
		return rootView;
	}
	
	// TODO - Make Bub's PARCELALBE
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		// Intent intent = new Intent(getActivity(), ViewEventFragment.class);
		// Will need to replace the layout's fragment or start a new activity
		// .... haven't decided.
		Bub selectedBub = (Bub) todays_events.get(position);
		String eventId = selectedBub.id;
		
		Intent intent = new Intent(getActivity(), ViewEventActivity.class);
		intent.putExtra(ViewEventActivity.EVENT_KEY, eventId);	
		startActivity(intent);
	}
	
	 @Override
	 public void onResume(){
		 super.onResume();
	     final Calendar c = Calendar.getInstance();
	     int day = c.get(Calendar.DATE);
	     int month = c.get(Calendar.MONTH) + MONTH_OFFSET;
	     int year = c.get(Calendar.YEAR);
	     String date = day + "/" + month + "/" + year;
	     todays_events = HubDatabase.FindEventByDate(date);
			
	     if(todays_events.size() != 0){
			adapter = new EventListAdapter(getActivity(), todays_events);
			setListAdapter(adapter);
		}
	 }
}
