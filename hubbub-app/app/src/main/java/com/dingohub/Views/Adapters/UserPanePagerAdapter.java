package com.dingohub.Views.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Fragments.MainUserFragments.UserGroupsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.TodaysBubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.UserBubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.UserHubsFragment;
import com.dingohub.Views.Fragments.MainUserFragments.UserProfileFragment;
import com.dingohub.Views.Fragments.MainUserFragments.SearchedEventsFragment;

/**
 * Created by ereio on 4/15/15.
 */
public class UserPanePagerAdapter extends FragmentPagerAdapter {

    // Tabs
    public final static String VIEW_TODAY = "Today";
    public final static String VIEW_HUBS = "Hubs";
    public final static String VIEW_BUBS = "Bubs";
    public final static String VIEW_GROUPS = "Groups";
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
                return new UserProfileFragment();
            case 1:
                return new TodaysBubsFragment();
            case 2:
                return new UserBubsFragment();
            case 3:
                return new UserHubsFragment();
            case 4:
                return new UserGroupsFragment();
            case 5:
                return new SearchedEventsFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
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