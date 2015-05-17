package com.dingohub.Model.Utilities;

import android.util.Log;
import android.widget.EditText;

import com.dingohub.Hubbub;
import com.dingohub.hubbub.R;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by ereio on 5/17/15.
 */
public class TimeManager {

    static public long PingInTimeInMillis(String startTime, String startDate, String pingin) {

        String[] stime = startTime.split("[: ]+");
        String[] sdate = startDate.split("/");

        int tappInVal = Integer.parseInt(pingin.split(" ")[0]);

        int[] tt = new int[5];
        //year
        tt[0] = Integer.parseInt(sdate[2].trim());
        //month (Jan = 0)
        tt[1] = Integer.parseInt(sdate[1].trim()) - 1;
        //day
        tt[2] = Integer.parseInt(sdate[0].trim());

        //hour
        if (stime[2].trim().equals("PM"))
            tt[3] = Integer.parseInt(stime[0].trim()) + 12;
        else
            tt[3] = Integer.parseInt(stime[0].trim());

        //minutes
        tt[4] = Integer.parseInt(stime[1].trim());

        Calendar cal = Calendar.getInstance();

        cal.set(tt[0], tt[1], tt[2], tt[3], tt[4], 0);
        cal.add(Calendar.HOUR_OF_DAY, -tappInVal);

        Log.i(Hubbub.TAG, DateFormat.getDateTimeInstance().format(cal.getTime()));
        Log.i(Hubbub.TAG,"Tapp in: "+tappInVal+" hrs before");

        return cal.getTimeInMillis();
    }

}
