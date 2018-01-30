package com.dingohub.Model.DataAccess;

/**
 * Created by ereio on 4/15/15.
 */
public class SharedPrefKeys {

    // shared preferences key for logins
    public static final String LOGIN_SETTINGS = "LoginSettings";

    // part of the Login Settings shared preferences
    // used for AutoLogin, doesn't need all user data
    public static final String USER_KEY = "UserKey";
    public static final String PASS_KEY = "PassKey";
    public static final String AUTO_LOG = "AutoLogin";
    public static final String LOC_KEY = "LocationKey";

    // shared preferences key for user settings
    public static final String USER_SETTINGS = "UserSettings";

        // Create a shared pref for showing real name
    public static final String SETTING_SHOW_NAME = "SettingShowName";
}
