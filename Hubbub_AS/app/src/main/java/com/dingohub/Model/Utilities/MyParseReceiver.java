package com.dingohub.Model.Utilities;


import com.dingohub.Views.Activities.ShowDialogActivity;
import com.parse.ParsePushBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyParseReceiver extends ParsePushBroadcastReceiver {

	public static final String DATA_KEY = "com.parse.Data";
	public static final String TAG = "MyParseReceiver";

    String eventID;
	/*
	@Override
	public void onPushOpen(Context context, Intent intent){
		Intent i = new Intent(context, ShowDialogAtivity.class);
		i.putExtras(intent.getExtras());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	*/

	@Override
	public void onReceive(Context context, Intent intent) {
			

        //Getting Push Data
        Bundle bundle = intent.getExtras();


        for (String key: bundle.keySet())
        {
           Log.d( "myApplication" , key + " is a key in the bundle");
        }

        if (bundle != null)
        {
            String data = bundle.getString(DATA_KEY);
            String[] splitData = data.split(":");
            String[] splitAgain = splitData[1].split(",");
            eventID = splitAgain[1].substring(1,splitAgain[1].length() );
           // intent = new Intent(context,ShowDialogActivity.class);
            //intent.putExtra(DATA_KEY, splitData[3]);
           // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //context.startActivity(intent);
        }

        Toast.makeText(context, "got notification"+eventID, Toast.LENGTH_LONG).show();


	}

}
