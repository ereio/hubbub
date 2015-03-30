package com.dingohub.Views.Fragments;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.dingohub.Views.Activities.ViewEventActivity;
import com.dingohub.Domain.DataAccess.Bub;
import com.dingohub.Domain.DataAccess.HubDatabase;
import com.dingohub.Domain.Utilities.EventListAdapter;

public class SearchedEventsFragment extends ListFragment {
	ArrayList<Bub> search_events;
	String tag = null;
	
	public SearchedEventsFragment(){}
	
	public SearchedEventsFragment(String passedTag) {
		tag = passedTag;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		// This is a defense against Events will Null tags (ONLY HAPPENS IN DEBUGGING)
		search_events = tag != null ? HubDatabase.FindEventByTag(tag) : new ArrayList<Bub>();

		EventListAdapter adapter = new EventListAdapter(getActivity(), search_events);
		setListAdapter(adapter);
		
	}
		
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		// Just need to send the Bub object to the activity
		// Can't just send it, needs to be parcelable.
		Bub a = (Bub) search_events.get(position);
		
		String eventId = a.id;
		Intent intent = new Intent(getActivity(), ViewEventActivity.class);
		
		// If keeping "Parse" service, needs to be parcelable
		intent.putExtra(ViewEventActivity.EVENT_KEY, eventId);
		startActivity(intent);
	}
}
