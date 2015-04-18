package com.dingohub;

import android.app.Application;

import com.dingohub.Model.DataAccess.HubDatabase;

public class Hubbub extends Application {
	public static String TAG = "Hubbub";
    public final static boolean DEBUG = true;

	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
