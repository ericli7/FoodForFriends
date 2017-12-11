package ericli.foodforfriends.viewholders;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ericli.foodforfriends.R;

/**
 * Created by ericli on 11/29/2017.
 */


public  class ViewHolderRequests extends RecyclerView.ViewHolder {
   public View _view_;

    public ViewHolderRequests(View itemView) {
        super(itemView);
        _view_ = itemView;
    }


    public void setStatus(String userStatus) {
        TextView textView = (TextView) _view_.findViewById(R.id.friend_request_status);
        textView.setText(userStatus);
    }

    public void setUserName(String userName) {
        TextView textView = (TextView) _view_.findViewById(R.id.friend_request_name);
        textView.setText(userName);
    }

    public void setUserThumbImage(final String thumbImage, final Context context) {

        final CircleImageView circleImageView = (CircleImageView) _view_.findViewById(R.id.friend_request_image);

        Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_profile).into(circleImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(thumbImage).placeholder(R.drawable.user_profile).into(circleImageView);
            }
        });
    }
}