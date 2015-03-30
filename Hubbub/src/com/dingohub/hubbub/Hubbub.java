package com.dingohub.hubbub;

import android.app.Application;

import com.dingohub.hub_database.HubDatabase;

public class Hubbub extends Application {
	
	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
