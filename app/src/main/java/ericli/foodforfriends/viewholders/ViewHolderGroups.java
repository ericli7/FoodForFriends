package ericli.foodforfriends.viewholders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ericli.foodforfriends.R;
/**
 * Created by ericli on 11/29/2017.
 */

public class ViewHolderGroups extends RecyclerView.ViewHolder {

    public View _view_;

    public ViewHolderGroups(View itemView) {
        super(itemView);
        _view_ = itemView;
    }

    public void setName(String name) {
        TextView textView = (TextView) _view_.findViewById(R.id.groupTitle);
        textView.setText(name);
    }


    public void setInfo(String info) {
        TextView textStatus = (TextView) _view_.findViewById(R.id.groupInfo);
        textStatus.setText(info);
    }


}