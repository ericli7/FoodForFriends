package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;

/**
 * Created by ericli on 11/29/2017.
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textName;
    private TextView textStatus;
    private CircleImageView image;

    private DatabaseReference dataReference;
    private DatabaseReference friendsRequestReference;
    private DatabaseReference friendsReference;

    private FirebaseAuth auth;

    String uid, currentState, friendUID;

    private Button btnSendRequest, btnDeleteRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");

        friendsRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friend_Request");
        friendsRequestReference.keepSynced(true);

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Chat_Friends");
        friendsReference.keepSynced(true);


        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        friendUID = getIntent().getExtras().get("user_id").toString();

        currentState = "not_friends";


        textName = (TextView) findViewById(R.id.text_name);
        textStatus = (TextView) findViewById(R.id.text_status);

        image = (CircleImageView) findViewById(R.id.image);
        btnDeleteRequest = (Button) findViewById(R.id.btn_deleterequeset);
        btnSendRequest = (Button) findViewById(R.id.btn_sendrequest);


        btnDeleteRequest.setOnClickListener(this);
        btnSendRequest.setOnClickListener(this);

        btnDeleteRequest.setVisibility(View.INVISIBLE);
        btnDeleteRequest.setEnabled(false);


        dataReference.child(friendUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                textName.setText(name);

                String status = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();
                textStatus.setText(status);

                String image = dataSnapshot.child(Const_and_Methods.User_Image).getValue().toString();
                Picasso.with(UserProfileActivity.this).load(image).placeholder(R.drawable.user_profile).into(UserProfileActivity.this.image);

                friendsRequestReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(friendUID)) {
                            String requestType = dataSnapshot.child(friendUID).child("request_type").getValue().toString();
                            if (requestType.equals("sent")) {

                                currentState = "request_sent";
                                btnSendRequest.setText("Cancel Request");

                                btnDeleteRequest.setVisibility(View.INVISIBLE);
                                btnDeleteRequest.setEnabled(false);

                            } else if (requestType.equals("receive")) {

                                currentState = "request_receive";
                                btnSendRequest.setText("Accept Request");

                                btnDeleteRequest.setVisibility(View.VISIBLE);
                                btnDeleteRequest.setEnabled(true);

                            }

                        } else {
                            friendsReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(friendUID)) {
                                        currentState = "friends";
                                        btnSendRequest.setText("UnFriend");

                                        btnDeleteRequest.setVisibility(View.INVISIBLE);
                                        btnDeleteRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendrequest:

                if (!uid.equals(friendUID)) {

                    btnSendRequest.setEnabled(false);
                    if (currentState.equals("not_friends")) {
                        sendFriendRequest();
                    }
                    if (currentState.equals("request_sent")) {
                        cancelFriendRequest();
                    }
                    if (currentState.equals("request_receive")) {
                        acceptFriendRequest();
                    }
                    if (currentState.equals("friends")) {
                        unFriend();
                    }
                } else {
                    btnDeleteRequest.setVisibility(View.INVISIBLE);
                    btnSendRequest.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_deleterequeset:
                final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Delete Request...");
                dialog.show();
                friendsRequestReference.child(uid).child(friendUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            friendsRequestReference.child(friendUID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        btnSendRequest.setEnabled(true);
                                        currentState = "not_friends";
                                        btnSendRequest.setText("Friend Request");
                                        btnDeleteRequest.setVisibility(View.INVISIBLE);
                                        btnDeleteRequest.setEnabled(false);

                                        dialog.dismiss();
                                    } else {
                                        final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Request Sent...");
                                        dialog.show();
                                    }
                                }
                            });
                        } else {
                            final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Request Sent...");
                            dialog.show();
                        }
                    }
                });
                break;
        }
    }

    private void unFriend() {
        final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Unfriend ...");
        dialog.show();
        friendsReference.child(uid).child(friendUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendsReference.child(friendUID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btnSendRequest.setEnabled(true);
                                currentState = "not_friends";
                                btnSendRequest.setText("Friend Request");
                                btnDeleteRequest.setVisibility(View.INVISIBLE);
                                btnDeleteRequest.setEnabled(false);
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

    private void sendFriendRequest() {
        final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Request Sent...");
        dialog.show();
        friendsRequestReference.child(uid).child(friendUID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendsRequestReference.child(friendUID).child(uid).child("request_type").setValue("receive").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                btnSendRequest.setEnabled(true);
                                currentState = "request_sent";
                                btnSendRequest.setText("Cancel Request");
                                btnDeleteRequest.setVisibility(View.INVISIBLE);
                                btnDeleteRequest.setEnabled(false);
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


    private void acceptFriendRequest() {
        final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Accept Request...");
        dialog.show();
        Calendar cal = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String getDat = currentDate.format(cal.getTime());

        friendsReference.child(uid).child(friendUID).child("date").setValue(getDat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendsReference.child(friendUID).child(uid).child("date").setValue(getDat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsRequestReference.child(uid).child(friendUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    friendsRequestReference.child(friendUID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnSendRequest.setEnabled(true);
                                                currentState = "friends";
                                                btnSendRequest.setText("Unfriend");

                                                btnDeleteRequest.setVisibility(View.INVISIBLE);
                                                btnDeleteRequest.setEnabled(false);
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

    private void cancelFriendRequest() {
        final AlertDialog dialog = new SpotsDialog(UserProfileActivity.this, "Cancel Request...");
        dialog.show();
        friendsRequestReference.child(uid).child(friendUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    friendsRequestReference.child(friendUID).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btnSendRequest.setEnabled(true);
                                currentState = "not_friends";
                                btnSendRequest.setText("Friend Request");
                                btnDeleteRequest.setVisibility(View.INVISIBLE);
                                btnDeleteRequest.setEnabled(false);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);

    }
}
