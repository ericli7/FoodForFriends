package ericli.foodforfriends.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import ericli.foodforfriends.activities.MessagesActivity;
import ericli.foodforfriends.models.ChatsModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;
import ericli.foodforfriends.viewholders.ViewHolderChats;
/**
 * Created by ericli on 11/29/2017.
 */


/**
 * the chatfragment shows all the friends so you can talk to them
 * */
public class ChatFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private DatabaseReference friendsReference, usersReference;

    private FirebaseAuth auth;


    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.chatList);

        auth = FirebaseAuth.getInstance();


        friendsReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friends").child(auth.getCurrentUser().getUid());
        friendsReference.keepSynced(true);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");
        usersReference.keepSynced(true);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChats();
    }

     /*
    * this method is used to load all the chats between friends.
    * firebaserecycleradapter is used for recyclerview and the data comes from firebase
    * adapter acts as a bridge between data and groupview, it grabs the data and sets the data into groupviews
    * */

    private void loadChats() {

        FirebaseRecyclerAdapter<ChatsModel, ViewHolderChats> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<ChatsModel, ViewHolderChats>(

                ChatsModel.class,
                R.layout.single_user,
                ViewHolderChats.class,
                friendsReference
        ) {
            @Override
            protected void populateViewHolder(final ViewHolderChats friendsViewHolder, ChatsModel friends, int i) {


                final String users_id = getRef(i).getKey();

                usersReference.child(users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                        String userThumb = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();
                        String userStatus = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnlineImage(userOnline);
                        }

                        friendsViewHolder.setUserName(userName);
                        friendsViewHolder.setUserStatus(userStatus);
                        friendsViewHolder.setUserThumbImage(userThumb, getContext());


                        friendsViewHolder._view_.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (dataSnapshot.child("online").exists()) {

                                    startActivity(new Intent(getContext(), MessagesActivity.class).putExtra("user_id", users_id).putExtra("user_name", userName));
                                    getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                                } else {
                                    usersReference.child(users_id).child("online").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(getContext(), MessagesActivity.class).putExtra("user_id", users_id).putExtra("user_name", userName));

                                            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                        }
                                    });
                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerView.setAdapter(friendsRecyclerViewAdapter);
    }


}
