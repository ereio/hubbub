package com.dingohub.Model.Utilities;


import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Views.Activities.DevActivities.MatViewBubActivity;
import com.dingohub.Views.Activities.ShowDialogActivity;
import com.dingohub.hubbub.R;
import com.parse.ParsePushBroadcastReceiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Random;

public class MyParseReceiver extends ParsePushBroadcastReceiver {

	public static final String DATA_KEY = "com.parse.Data";
	public static final String TAG = "MyParseReceiver";
    public static final String EVENT_KEY = "asduiuneukluhasd";

    String eventID;
    String pushType;
    Hub hub;
    Bub event;
    HubUser user;


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
            Log.d("myApplication", pushType);

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


                int randomNum = rand.nextInt((50 - 5) + 1) + 5;

                notificationmanager.notify(randomNum, notificationbuilder.build());
            }

            check_invite_notification(context, intent);


        }
    }

    private void check_invite_notification(Context context, Intent intent){
        if(pushType.equals("FIND PUSH TYPE")){
            intent = new Intent(context, MatViewBubActivity.class);
            intent.putExtra(EVENT_KEY, eventID);
            user = HubDatabase.GetUserById(pushType);
            event = HubDatabase.GetBubFromId(eventID);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.hubbub_logo)
                    .setContentTitle("New invitation")
                    .setContentText(user.username + "invited you to " + event.title)
                    .setContentIntent(pendingIntent);

            notificationBuilder.setAutoCancel(true);
            NotificationManagerCompat nm = NotificationManagerCompat.from(context);

            Random rand = new Random();
            int num = rand.nextInt(100000);

            nm.notify(num, notificationBuilder.build());


        }
    }

}
