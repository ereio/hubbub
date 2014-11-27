package com.dingohub.tools;

import org.json.JSONException;
import org.json.JSONObject;


import com.dingohub.activities_user.CreateEventsActivity;
import com.parse.ParsePush;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private String event_id;

	@Override
	public void onReceive(Context context, Intent intent) {
	   
		Toast.makeText(context.getApplicationContext(), "Beginning Tapp-In !!!", Toast.LENGTH_SHORT).show();
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


