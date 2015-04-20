package com.dingohub.Views.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Fragments.MainUserFragments.MatUserGroupsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.MatTodaysBubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.MatUserBubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.MatUserHubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.MatUserProfileFragment;

/**
 * Created by ereio on 4/15/15.
 */
public class UserPanePagerAdapter extends FragmentPagerAdapter {

    // Tabs
    public final static String VIEW_TODAY = "Today";
    public final static String VIEW_HUBS = "Hubs";
    public final static String VIEW_BUBS = "Bubs";
    public final static String VIEW_GROUPS = "Groups";
    public final static String VIEW_SETTINGS = "Settings";
    public final static String VIEW_UNKNOWN_PAGE = "Default Page Title";
    public static String VIEW_PROFILE = "Default Profile";

    public static String [] TABS;

    public UserPanePagerAdapter(FragmentManager fm, String username) {
        super(fm);
        VIEW_PROFILE = username;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString(Hubbub.USER_VIEW_KEY, HubDatabase.GetCurrentUser().id);
                return new MatUserProfileFragment();
            case 1:
                return new MatTodaysBubsFragment();
            case 2:
                return new MatUserBubsFragment();
            case 3:
                return new MatUserHubsFragment();
            case 4:
                return new MatUserGroupsFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    // Implemented to modulate pages separate from current Tab implementation
    // Precaution against ActionBarTab Deprecation
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return VIEW_PROFILE;
            case 1:
                return VIEW_TODAY;
            case 2:
                return VIEW_BUBS;
            case 3:
                return VIEW_HUBS;
            case 4:
                return VIEW_GROUPS;
            default:
                return VIEW_UNKNOWN_PAGE;
        }
    }
}