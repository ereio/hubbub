package com.dingohub;

import android.app.Application;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.hubbub.R;

public class Hubbub extends Application {
	public static String TAG = "Hubbub ";
    public static final String USER_VIEW_KEY = "FETCH_USER_VIEW";
    public final static boolean DEBUG = false;

    public static int color_array[] = {R.color.ColorAccent, R.color.ColorPrimary, R.color.ColorPrimaryDark, R.color.Khaki,
            R.color.DarkTurquoise, R.color.DarkSeaGreen};

	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
