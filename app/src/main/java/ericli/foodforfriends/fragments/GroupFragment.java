package ericli.foodforfriends.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ericli.foodforfriends.activities.NewGroupActivity;
import ericli.foodforfriends.activities.GroupMessagesActivity;
import ericli.foodforfriends.models.GroupsModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.viewholders.ViewHolderGroups;
/**
 * Created by ericli on 11/29/2017.
 */

// groupfragment loads the groups that you a member of
public class GroupFragment extends Fragment {

    private View view;


    private RecyclerView recyclerView;


    private DatabaseReference modelGroupRef;
    private DatabaseReference groupChatRef;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.groupList);


        modelGroupRef = FirebaseDatabase.getInstance().getReference().child("model_group").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        modelGroupRef.keepSynced(true);


        groupChatRef = FirebaseDatabase.getInstance().getReference().child("Group_Chat");
        groupChatRef.keepSynced(true);


        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);


        recyclerView.setLayoutManager(layoutManager);

        view.findViewById(R.id.floatActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewGroupActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        return view;

    }


    @Override
    public void onStart() {
        super.onStart();
        loadGroups();
    }


    private void loadGroups() {

        FirebaseRecyclerAdapter<GroupsModel, ViewHolderGroups> recyclerViewAdapter = new FirebaseRecyclerAdapter<GroupsModel, ViewHolderGroups>(

                GroupsModel.class,
                R.layout.single_item_group,
                ViewHolderGroups.class,
                modelGroupRef
        ) {
            @Override
            protected void populateViewHolder(final ViewHolderGroups viewHolder, final GroupsModel model, final int i) {


                final String users_id = getRef(i).getKey();

                  groupChatRef.child(users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        
                        final String groupName = dataSnapshot.child("name").getValue().toString();
                        String groupAdminName = dataSnapshot.child("admin").getValue().toString();
                        String groupTotalMember = dataSnapshot.child("totalmember").getValue().toString();

                        viewHolder.setName(groupName);
                        viewHolder.setInfo("Created By:  " + groupAdminName + "\nMemeber:  " + groupTotalMember);

                        viewHolder._view_.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), GroupMessagesActivity.class).putExtra("groupid", users_id).putExtra("title", groupName));
                                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerView.setAdapter(recyclerViewAdapter);
    }


}
