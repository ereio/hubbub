package com.dingohub.Model.Utilities;


import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Activities.DevActivities.MatViewBubActivity;
import com.dingohub.Views.Activities.ShowDialogActivity;

import com.dingohub.hubbub.R;
import com.parse.ParsePushBroadcastReceiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class MyParseReceiver extends ParsePushBroadcastReceiver {

	public static final String DATA_KEY = "com.parse.Data";
	public static final String TAG = "MyParseReceiver";
    public static final String EVENT_KEY = "asduiuneukluhasd";
    Bub event;
    String eventID;
    String pushType;
    Hub hub;


	@Override
	public void onReceive(Context context, Intent intent) {


        //Getting Push Data
        Bundle bundle = intent.getExtras();


        if (bundle != null) {
            String data = bundle.getString(DATA_KEY);


            String[] splitData = data.split(",");
            String[] splitType = splitData[0].split(":");

            String[] splitEvent = splitData[1].split(":");

            pushType = splitType[1].replaceAll("\"", "");
            eventID = splitEvent[1].replaceAll("\"", "");
            Log.d("myApplication",pushType);

            if (pushType.equals("1")) {
                intent = new Intent(context, ShowDialogActivity.class);
                intent.putExtra(DATA_KEY, eventID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

            } else {

                intent = new Intent(context,MatViewBubActivity.class);
                intent.putExtra(EVENT_KEY,eventID);
                hub = HubDatabase.GetHubFromId(pushType);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                event = HubDatabase.GetBubsById(eventID);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.hubbub_logo)
                        .setContentTitle(hub.name + " Hub")
                        .setContentText("new event created:  " + event.title)
                        .setContentIntent(pendingIntent);

                notificationbuilder.setAutoCancel(true);
                NotificationManagerCompat notificationmanager =
                        NotificationManagerCompat.from(context);

                Random rand = new Random();


                int randomNum = rand.nextInt((20 - 5) + 1) + 5;

                notificationmanager.notify(randomNum, notificationbuilder.build());



            }




        }
    }

}
