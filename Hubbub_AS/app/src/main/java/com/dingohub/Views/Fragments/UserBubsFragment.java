package com.dingohub.Views.Fragments;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dingohub.Views.Activities.ViewEventActivity;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.hubbub.R;
import com.dingohub.Model.Utilities.FollowedEventListAdapter;

public class UserBubsFragment extends ListFragment {
	ArrayList<Bub> followed_events;
	FollowedEventListAdapter adapter;
	
	public UserBubsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_bubs, container,
				false);
		return rootView;
	}
	
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		// Just need to send the Bub object to the activity
		// Can't just send it, needs to be parcelable.
		Bub a = (Bub) followed_events.get(position);
		
		// CREATE PARCELALBE SO NO NEED TO REQUERY ITEMS
		String eventId = a.id;
		
		Intent intent = new Intent(getActivity(), ViewEventActivity.class);
		intent.putExtra(ViewEventActivity.EVENT_KEY, eventId);					// MAKE Bub parcelable
		startActivity(intent);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//followed_events = HubDatabase.FindBubByFollower(HubDatabase.getCurrentUser().id);
        followed_events = HubDatabase.FindBubsFollowed(HubDatabase.getCurrentUser().id);
		
		if(followed_events != null &&followed_events.size() != 0 ){
			adapter = new FollowedEventListAdapter(getActivity(), followed_events);
			setListAdapter(adapter);
		}
	}
}
