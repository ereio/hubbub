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

    public static int polygons[] = {R.drawable.polygon1, R.drawable.polygon2, R.drawable.polygon3, R.drawable.polygon4, R.drawable.polygon5};

	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
