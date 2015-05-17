package com.dingohub.Views.Fragments.MatBaseFragments;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Views.Activities.DevActivities.ViewBubActivity;
import com.dingohub.Views.Activities.DevActivities.ViewHubActivity;

import java.util.ArrayList;

/**
 * Created by ereio on 4/20/15.
 */
public class MatBaseFragment extends Fragment {

    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener{

        private static final int BUB_TYPE = 1;
        private static final int HUB_TYPE = 2;
        private static final int USER_TYPE = 3;

        private GestureDetector mGestureDetector;
        private ArrayList<Bub> bubArrayList;
        private ArrayList<Hub> hubArrayList;
        private ArrayList<HubUser> hubUsers;

        private int currentType = 0;

        BubRecyclerViewListener(GestureDetector mGestureDectector, ArrayList<?> list){
            this.mGestureDetector = mGestureDectector;
            get_list_type(list);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)) {

                if (Hubbub.DEBUG)
                    Toast.makeText(getActivity().getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                if (currentType == BUB_TYPE) {
                    String eventId = bubArrayList.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getActivity(), ViewHubActivity.class);
                    intent.putExtra(ViewBubActivity.EVENT_KEY, eventId);                    // MAKE Bub parcelable
                    startActivity(intent);
                } else if (currentType == HUB_TYPE){

                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        private void get_list_type(ArrayList<?> list){
            if(list.getClass().equals(Bub.class)){
                bubArrayList = (ArrayList<Bub>) list;
                currentType = BUB_TYPE;
            } else if (list.getClass().equals(Hub.class)){
                bubArrayList = (ArrayList<Bub>) list;
                currentType = HUB_TYPE;
            } else if (list.getClass().equals(HubUser.class)) {
                bubArrayList = (ArrayList<Bub>) list;
                currentType = USER_TYPE;
            }
        }

    }


}
