package ericli.foodforfriends.viewholders;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ericli.foodforfriends.R;

/**
 * Created by ericli on 11/29/2017.
 */



public class ViewHolderUsersForCreateGroup extends RecyclerView.ViewHolder {

    public View _view_;

    public CheckBox checkBox;


    public ViewHolderUsersForCreateGroup(View itemView) {
        super(itemView);
        _view_ = itemView;

        checkBox = (CheckBox) _view_.findViewById(R.id.checkbox);
    }

    public void setThumbImage(final String thumbImage, final Context context) {

        final CircleImageView circleImageView = (CircleImageView) _view_.findViewById(R.id.user_single_image);

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

    public void setStatus(String status) {
        TextView textView = (TextView) _view_.findViewById(R.id.user_single_status);
        textView.setText(status);

    }


    public void setName(String name) {
        TextView textView = (TextView) _view_.findViewById(R.id.user_single_name);
        textView.setText(name);

    }

}

