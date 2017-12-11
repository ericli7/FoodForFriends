package ericli.foodforfriends.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ericli.foodforfriends.models.UsersModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.viewholders.ViewHolderUsers;

/**
 * Created by ericli on 11/29/2017.
 */

/**
 * when you click on optionmenu item allusers, this activity starts
 * this activity shows all the users that are currently using this application
 * */
public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private DatabaseReference dataReference;


    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");
        dataReference.keepSynced(true);


        recyclerView = (RecyclerView) findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    // save RecyclerView state
    @Override
    protected void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    // restore RecyclerView state
    @Override
    protected void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUsers();

    }

    /*
    * this method is used to load the all users using firebaseRecycleradapter
    * */
    private void loadUsers() {
        FirebaseRecyclerAdapter<UsersModel, ViewHolderUsers> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersModel, ViewHolderUsers>(
                UsersModel.class,
                R.layout.single_user,
                ViewHolderUsers.class,
                dataReference
        ) {

            @Override
            protected void populateViewHolder(ViewHolderUsers usersViewHolder, UsersModel users, int position) {

                usersViewHolder.setName(users.getName());
                usersViewHolder.setStatus(users.getStatus());
                usersViewHolder.setThumbImage(users.getThumb_image(), getApplicationContext());

                final String user_id_of_click = getRef(position).getKey();
                usersViewHolder._view_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(UsersActivity.this, UserProfileActivity.class).putExtra("user_id", user_id_of_click));
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

    }

}
