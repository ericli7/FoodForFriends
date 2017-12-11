package ericli.foodforfriends.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ericli.foodforfriends.fragments.ChatFragment;
import ericli.foodforfriends.fragments.FriendFragment;
import ericli.foodforfriends.fragments.GroupFragment;
import ericli.foodforfriends.fragments.RequestFragment;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* this is used to load different fragments inside the viewpager when you click on tabs
* */
public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    public ViewPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    // this return a fragment according to the position click of tabview
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;
            case 3:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;

            default:
                return null;
        }
    }

    //this returns the title of the page
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Requests";
            case 1:
                return "Chat";
            case 2:
                return "Group";
            case 3:
                return "Friends";

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}