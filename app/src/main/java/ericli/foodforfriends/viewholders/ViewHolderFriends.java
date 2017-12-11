package ericli.foodforfriends.viewholders;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ericli.foodforfriends.R;

/**
 * Created by ericli on 11/29/2017.
 */


public  class ViewHolderFriends extends RecyclerView.ViewHolder {

    public View _view_;

    public ViewHolderFriends(View itemView) {
        super(itemView);
        _view_ = itemView;

    }

    public void setDateSinceFriend(String date) {

        TextView textView = (TextView) _view_.findViewById(R.id.user_single_status);
        textView.setText("Friends Since: " + date);

    }


    public void setUserOnlineStatus(String onlineStatus) {

        ImageView imageView = (ImageView) _view_.findViewById(R.id.user_single_online_icon);

        if (onlineStatus.equals("true")) {

            imageView.setVisibility(View.VISIBLE);

        } else {

            imageView.setVisibility(View.INVISIBLE);
        }

    }


    public void setFriendName(String name) {

        TextView textView = (TextView) _view_.findViewById(R.id.user_single_name);
        textView.setText(name);

    }

    public void setFriendThumbImage(final String thumbImage, final Context context) {

        final CircleImageView cirImage = (CircleImageView) _view_.findViewById(R.id.user_single_image);

        Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_profile).into(cirImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(thumbImage).placeholder(R.drawable.user_profile).into(cirImage);
            }
        });

    }


}