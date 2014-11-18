package com.dingohub.hubbub;

import com.dingohub.hub_database.HubDatabase;
import com.parse.Parse;

import android.app.Application;
import android.util.Log;

public class Hubbub extends Application {
	
	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
