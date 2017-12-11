package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.models.Chat_Friends;
import ericli.foodforfriends.models.UsersModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;
import ericli.foodforfriends.viewholders.ViewHolderUsersForCreateGroup;

/**
 * Created by ericli on 11/29/2017.
 */

/**
 * used to create a new group
 * */
public class NewGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editGroupName;
    private RecyclerView recyclerView;
    private DatabaseReference usersReference;
    private DatabaseReference friendsRefererence;
    private DatabaseReference groupDataReference;
    private DatabaseReference modelGroupReference;


    String adminName = "";

    List<String> list = new ArrayList<>();
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        list.add(uid);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");
        usersReference.keepSynced(true);

        friendsRefererence = FirebaseDatabase.getInstance().getReference().child("Chat_Friends").child(uid);
        friendsRefererence.keepSynced(true);

        groupDataReference = FirebaseDatabase.getInstance().getReference().child("Group_Chat");
        groupDataReference.keepSynced(true);


        modelGroupReference = FirebaseDatabase.getInstance().getReference().child("model_group");
        modelGroupReference.keepSynced(true);

        editGroupName = (EditText) findViewById(R.id.editGroupName);

        recyclerView = (RecyclerView) findViewById(R.id.userSelect);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        findViewById(R.id.btnCreateGroup).setOnClickListener(this);


        friendsRefererence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot data : dataSnapshot.getChildren()) {


                    usersReference.child(data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            listCheck.add(dataSnapshot.getValue(UsersModel.class));

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


//        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                    listCheck.add(data.getValue(UsersModel.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }


    List<UsersModel> listCheck = new ArrayList<>();

    /**
     * this method is used to load the firebase database inside recyclerview using firebaserecycleradapter. this comes from firebaseui.
     * when something changes firebaseui automatically changes database and then loads it in refreshing it
     *
     * */


    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Chat_Friends, ViewHolderUsersForCreateGroup> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat_Friends, ViewHolderUsersForCreateGroup>(
                Chat_Friends.class,
                R.layout.single_userselection_forgroup,
                ViewHolderUsersForCreateGroup.class,
                friendsRefererence   //  usersReference
        ) {

            @Override
            protected void populateViewHolder(final ViewHolderUsersForCreateGroup usersViewHolder, final Chat_Friends users, final int position) {


                final String usersID = getRef(position).getKey();

                usersReference.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                        String thumbImage = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();
                        String status = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();

                        usersViewHolder.setName(name);
                        usersViewHolder.setStatus(status);
                        usersViewHolder.setThumbImage(thumbImage, getApplicationContext());


                        if (usersID == uid) {
                            adminName = name;
                        }


                        usersViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                listCheck.get(position).setSelected(isChecked);
                                if (isChecked) {
                                    list.add(usersID);

                                } else {
                                    list.remove(usersID);

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


        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
        //   finish();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnCreateGroup) {
            createGroup();
        }
    }

    /*
    *
    * this method creates the new group
    * **/

    private void createGroup() {
        final String date = new SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().getTime());
        if (!TextUtils.isEmpty(editGroupName.getText().toString())) {
            if (list.size() > 1) {
                final AlertDialog dialog = new SpotsDialog(NewGroupActivity.this, "Creating New Group...");
                dialog.show();

                final String pushKey = groupDataReference.push().getKey();
                Map<String, String> map = new HashMap<>();
                map.put("name", editGroupName.getText().toString());
                map.put("admin", adminName);
                map.put("totalmember", String.valueOf(list.size()));
                groupDataReference.child(pushKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            for (int i = 0; i < list.size(); i++) {
                                final int I = i;
                                modelGroupReference.child(list.get(i)).child(pushKey).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (I == (list.size() - 1)) {

                                            dialog.dismiss();
                                            onBackPressed();
                                        }
                                    }
                                });
                            }

                        } else {
                            dialog.dismiss();
                            Const_and_Methods.Alert(NewGroupActivity.this, "Error", "Something went wrong check internet connection");
                        }
                    }
                });


            } else {
                Const_and_Methods.Alert(NewGroupActivity.this, "Empty Group Memeber", "Select Group Member to create Group");
            }
        } else {
            Const_and_Methods.Alert(NewGroupActivity.this, "Empty Group Name", "Enter Group Number first");
        }
    }
}
