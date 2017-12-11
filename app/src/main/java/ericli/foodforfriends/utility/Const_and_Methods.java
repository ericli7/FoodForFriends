package ericli.foodforfriends.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.tapadoo.alerter.Alerter;

import ericli.foodforfriends.R;

/**
 * Created by ericli on 11/29/2017.
 */
public class Const_and_Methods {

    public static final int  REQUEST_PICK_IMAGE = 1;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static boolean isNetworkAvaliable(Context _context) {
        ConnectivityManager _connectivityManager = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((_connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && _connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (_connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && _connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void Alert(Context _context, String title, String content) {
        Alerter.create((Activity) _context)
                .setTitle(title)
                .setText(content)
                .setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.mipmap.ic_launcher)
                .setDuration(1700)
                .show();
    }

    public static String User_Image="User_Image";

    public static String User_Name="User_Name";

    public static String User_Status="User_Status";

    public static String User_thumb_Image="User_thumb_Image";
}