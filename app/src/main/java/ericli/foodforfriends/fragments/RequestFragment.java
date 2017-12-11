package ericli.foodforfriends.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.models.RequestsModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;
import ericli.foodforfriends.viewholders.ViewHolderRequests;
/**
 * Created by ericli on 11/29/2017.
 */

/*
this fragment loads the send and receive friend request for user

* */
public class RequestFragment extends Fragment {


    private RecyclerView recyclerView;
    private View view;

    private DatabaseReference usersReference;
    private DatabaseReference friendsDataReference;
    private DatabaseReference friendsRequestReference, friendsRequestDataReference;

    private FirebaseAuth auth;
    String uid;


    public RequestFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerRequestList);

        recyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        friendsRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friend_Request").child(uid);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");

        friendsDataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friends");
        friendsRequestDataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friend_Request");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadRequests();

    }

       /*
    * this method is used to load the all friend request that you send and that you receive from someone
    * */


    private void loadRequests() {
        FirebaseRecyclerAdapter<RequestsModel, ViewHolderRequests> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RequestsModel, ViewHolderRequests>(
                RequestsModel.class, R.layout.friend_requests_layout, ViewHolderRequests.class, friendsRequestReference

        ) {
            @Override
            protected void populateViewHolder(final ViewHolderRequests viewHolder, RequestsModel model, int position) {

                final String users_id = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String requestType = dataSnapshot.getValue().toString();

                            if (requestType.equals("receive")) {


                                TextView textView = (TextView) viewHolder._view_.findViewById(R.id.friend_send_receive);
                                textView.setText("Receive");

                                usersReference.child(users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                                        String userStatus = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();
                                        String userThumb = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setUserThumbImage(userThumb, getContext());
                                        viewHolder.setStatus(userStatus);

                                        viewHolder._view_.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence clickOptions[] = new CharSequence[]{"Accept Friend Request", "Cancel Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setIcon(R.drawable.logo);

                                                builder.setTitle("Request Options");
                                                builder.setItems(clickOptions, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int clickPosition) {


                                                        if (clickPosition == 0) {
                                                            final android.app.AlertDialog dialog = new SpotsDialog(getContext(), "Accept Request...");
                                                            dialog.show();
                                                            Calendar cal = Calendar.getInstance();
                                                            final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String getDat = currentDate.format(cal.getTime());

                                                            friendsDataReference.child(uid).child(users_id).child("date").setValue(getDat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    friendsDataReference.child(users_id).child(uid).child("date").setValue(getDat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            friendsRequestDataReference.child(uid).child(users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()) {
                                                                                        friendsRequestDataReference.child(users_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Const_and_Methods.Alert(getContext(), "Friend Request Accepted", "You both are friends Now! Congratulations");
                                                                                                    dialog.dismiss();
                                                                                                } else {
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }

                                                        if (clickPosition == 1) {
                                                            final android.app.AlertDialog dialog = new SpotsDialog(getContext(), "Cancel Request...");
                                                            dialog.show();
                                                            friendsRequestDataReference.child(uid).child(users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        friendsRequestDataReference.child(users_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Const_and_Methods.Alert(getContext(), "Friend Request Cancelled", "Successfully Cancelled Friend Request");
                                                                                    dialog.dismiss();
                                                                                } else {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
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

                            } else if (requestType.equals("sent")) {

                                TextView textView = (TextView) viewHolder._view_.findViewById(R.id.friend_send_receive);
                                textView.setText("Sent");


                                usersReference.child(users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String userName = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                                        String userThumb = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();
                                        String userStatus = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setUserThumbImage(userThumb, getContext());
                                        viewHolder.setStatus(userStatus);


                                        viewHolder._view_.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                CharSequence clickOptions[] = new CharSequence[]{"Cancel Friend Request"};
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Request Sent");
                                                builder.setItems(clickOptions, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int clickposition) {


                                                        if (clickposition == 0) {
                                                            final android.app.AlertDialog dialog = new SpotsDialog(getContext(), "Request Sent...");
                                                            dialog.show();

                                                            friendsRequestDataReference.child(uid).child(users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        friendsRequestDataReference.child(users_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Const_and_Methods.Alert(getContext(), "Friend Request Cancelled", "Successfully Cancelled Friend Request");
                                                                                    dialog.dismiss();
                                                                                } else {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
