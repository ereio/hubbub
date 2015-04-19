package com.dingohub;

import android.app.Application;

import com.dingohub.Model.DataAccess.HubDatabase;

public class Hubbub extends Application {
	public static String TAG = "Hubbub ";
    public static final String USER_VIEW_KEY = "FETCH_USER_VIEW";
    public final static boolean DEBUG = true;

	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
