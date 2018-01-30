package com.dingohub.Views.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Views.Activities.DevActivities.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ereio on 5/17/15.
 */
public class RecyclerManager {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    public class UserRecycleViewListener implements RecyclerView.OnItemTouchListener {
        List<HubUser> followers;
        Activity activity;
        GestureDetector mGestureDetector;

        public UserRecycleViewListener(GestureDetector mGestureDectector, ArrayList<HubUser> users) {
            this.mGestureDetector = mGestureDectector;
            followers = users;
        }

        public void AddItems(List<HubUser> users) {

        }

        public void AddItem(HubUser user){

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && mGestureDetector.onTouchEvent(e)) {

                if (Hubbub.DEBUG)
                    Toast.makeText(activity.getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // Find the friend idea of the person clicked
                if (followers.size() != 0) {
                    String followerId = followers.get(rv.getChildPosition(child)).id;

                    // creates a bundle with the friend id in the new fragment
                    Intent intent = new Intent(activity.getApplicationContext(), UserProfileActivity.class);
                    intent.putExtra(Hubbub.USER_VIEW_KEY, followerId);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
