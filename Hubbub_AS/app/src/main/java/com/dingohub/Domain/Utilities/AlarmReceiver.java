package com.dingohub.Domain.Utilities;

import org.json.JSONException;
import org.json.JSONObject;


import com.dingohub.Views.Activities.CreateEventsActivity;
import com.parse.ParsePush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	private String event_id;

	@Override
	public void onReceive(Context context, Intent intent) {
	   
		
		event_id = intent.getStringExtra(CreateEventsActivity.CHANNEL_KEY);

		ParsePush push = new ParsePush();


		push.setMessage("Hello");

		push.setChannel(event_id);
		
		try {
			push.setData(new JSONObject().put(MyParseReceiver.DATA_KEY, event_id));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		push.sendInBackground();
		
		
        }
		
	}


