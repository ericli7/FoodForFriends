package ericli.foodforfriends.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ericli.foodforfriends.models.GroupMessagesModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* this is the adapter for groupmessage
* */

public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.MessageViewHolder> {


    private List<GroupMessagesModel> list;

    private DatabaseReference usersReference;


    Context context;

    public GroupMessagesAdapter(List<GroupMessagesModel> message_List, Context context) {

        this.list = message_List;
        this.context = context;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        usersReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public TextView name;
        public TextView time;
        public TextView message;
        public ImageView messageImageView;


        public MessageViewHolder(View view) {
            super(view);


            name = (TextView) view.findViewById(R.id.name_text_layout);
            time = (TextView) view.findViewById(R.id.time_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            message = (TextView) view.findViewById(R.id.message_text_layout);
            messageImageView = (ImageView) view.findViewById(R.id.messageImageView);


        }
    }

    // onbindviewholder is used to bind the data to recyclerview items

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {


        final GroupMessagesModel model = list.get(i);

        String fromUser = model.getUid();


        viewHolder.message.setText(model.getMessage());
        viewHolder.time.setText(Const_and_Methods.getTimeAgo(model.getTime(), context));
        if (model.getImageUrl() != null) {
            viewHolder.messageImageView.setVisibility(View.VISIBLE);
            viewHolder.message.setVisibility(View.GONE);
            try {


                Picasso.with(context).load(model.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_placeholder_image).into(viewHolder.messageImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(model.getImageUrl()).placeholder(R.drawable.ic_placeholder_image).into(viewHolder.messageImageView);
                    }
                });

            } catch (IllegalArgumentException e) {
                viewHolder.message.setText("Error While loading image");

            }

        } else {

            viewHolder.messageImageView.setVisibility(View.GONE);
            viewHolder.message.setVisibility(View.VISIBLE);

        }

        usersReference.child(fromUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                viewHolder.name.setText(dataSnapshot.child("User_Name").getValue().toString());

                Picasso.with(viewHolder.profileImage.getContext()).load(dataSnapshot.child("User_thumb_Image").getValue().toString())
                        .placeholder(R.drawable.user_profile).into(viewHolder.profileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    // this method is used to filter the messages while searching
    public void setFilter(ArrayList<GroupMessagesModel> messageList) {
        this.list = new ArrayList<>();
        this.list.addAll(messageList);
        notifyDataSetChanged();
    }

    // this returns the number of messages

    @Override
    public int getItemCount() {
        return list.size();
    }


}
