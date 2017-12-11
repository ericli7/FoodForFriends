package ericli.foodforfriends.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import ericli.foodforfriends.activities.UserProfileActivity;
import ericli.foodforfriends.models.Chat_Friends;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;
import ericli.foodforfriends.viewholders.ViewHolderFriends;
/**
 * Created by ericli on 11/29/2017.
 */


/*
* friendfragment loads all the friends that you have
* */
public class FriendFragment extends Fragment {


    private RecyclerView recyclerView;

    private DatabaseReference friendsReference;
    private DatabaseReference usersReference;


    private View view;


    public FriendFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.friends_list);


        friendsReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        loadFriends();

    }


    //loadfriends loads your friends via firebase


    private void loadFriends() {
        FirebaseRecyclerAdapter<Chat_Friends, ViewHolderFriends> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chat_Friends, ViewHolderFriends>(

                Chat_Friends.class,
                R.layout.users_single,
                ViewHolderFriends.class,
                friendsReference

        ) {
            @Override
            protected void populateViewHolder(final ViewHolderFriends friendsViewHolder, Chat_Friends friends, int i) {

                friendsViewHolder.setDateSinceFriend(friends.getDate());

                final String users_id = getRef(i).getKey();

                usersReference.child(users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                        String userThumbImage = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnlineStatus(userOnline);

                        }

                        friendsViewHolder.setFriendName(userName);
                        friendsViewHolder.setFriendThumbImage(userThumbImage, getContext());

                        friendsViewHolder._view_.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setIcon(R.drawable.logo);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int clickPosition) {



                                        if (clickPosition == 0) {


                                            startActivity(new Intent(getContext(), UserProfileActivity.class).putExtra("user_id", users_id));
                                            getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                                        }

                                        if (clickPosition == 1) {

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

                                    }
                                });

                                builder.show();

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
