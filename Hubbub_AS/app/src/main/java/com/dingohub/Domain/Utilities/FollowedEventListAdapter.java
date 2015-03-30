package com.dingohub.Domain.Utilities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.Domain.DataAccess.Bub;
import com.dingohub.hubbub.R;



public class FollowedEventListAdapter extends ArrayAdapter<Bub> {
		private final Context context;
		private final ArrayList<Bub> tEvents;
		
		public FollowedEventListAdapter(Context context, ArrayList<Bub> events) {
			super(context, R.layout.listitem_followed_bub, events);
			this.context = context;
			tEvents = events;
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isEnabled(int position){
			super.isEnabled(position);
			return true;
		}
		
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.listitem_followed_bub, parent , false);
			
			//	TextView event_tags = (TextView) findViewById(R.id.eventview_tags);;
			//	TextView event_following = (TextView) findViewById(R.id.eventview_following_list);
			
			//	TextView event_tappin = (TextView) findViewById(R.id.eventview_tappin_time);
			
			TextView event_name = (TextView) row.findViewById(R.id.listitem_fbub_title);
			TextView event_location = (TextView) row.findViewById(R.id.listitem_fbub_location);
			
			TextView event_start_time = (TextView) row.findViewById(R.id.listitem_fbub_start_time);
			TextView event_end_time = (TextView) row.findViewById(R.id.listitem_fbub_end_time);
			TextView event_start_date = (TextView) row.findViewById(R.id.listitem_fbub_start_date);

			TextView event_details = (TextView) row.findViewById(R.id.text_about);
			TextView event_num_following = (TextView) row.findViewById(R.id.listitem_fbub_num_following);
			ImageView event_picture = (ImageView) row.findViewById(R.id.listitem_fbub_picture);
			
			
			// Gets the event needed from array in class
			Bub event = tEvents.get(position);
			
			if(event.title != null)
				event_name.setText(event.title);
			
			if(event.location != null)
				event_location.setText(event.location);
			
			if(event.start_time != null)
				event_start_time.setText(event.start_time);
			
			if(event.start_date != null)
				event_start_date.setText(event.start_date);
			
			if(event.end_time != null)
				event_end_time.setText(event.end_time);
			
			if(event.details != null)
				event_details.setText(event.details);
			
			if(event.follower_ids != null)
				event_num_following.setText(String.valueOf(event.follower_ids.length()));
			
			if(event.picture != null){
				BitmapWorker worker = new BitmapWorker(event_picture, event.picture, 250, 250);
				worker.execute(0);
			}
			
			
			return row;
		}

	}
