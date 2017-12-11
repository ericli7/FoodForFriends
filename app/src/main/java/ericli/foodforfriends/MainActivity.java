package ericli.foodforfriends;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import ericli.foodforfriends.activities.LoginUserActivity;
import ericli.foodforfriends.activities.AccountSettingActivity;
import ericli.foodforfriends.activities.UsersActivity;
import ericli.foodforfriends.adapters.ViewPagerFragmentAdapter;

/**
 * Created by ericli on 11/29/2017.
 */


public class MainActivity extends AppCompatActivity {


    private DatabaseReference usersReference;


    private ViewPagerFragmentAdapter sectionPagerAdapter;

    private ViewPager viewPager;
    private FirebaseAuth auth;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionPagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            usersReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users").child(auth.getCurrentUser().getUid());
        }


    }

    private void loginPage() {

        startActivity(new Intent(MainActivity.this, LoginUserActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser _currentUser = auth.getCurrentUser();
        if (_currentUser == null) {
            loginPage();
        } else {
            usersReference.child("online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {

            usersReference.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                if (auth.getCurrentUser() != null) {
                    usersReference.child("online").setValue(ServerValue.TIMESTAMP);
                }
                auth.signOut();
                loginPage();
                break;
            case R.id.menu_setting:
                Intent intentProfile = new Intent(MainActivity.this, AccountSettingActivity.class);
                startActivity(intentProfile);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case R.id.menu_users:
                Intent intentUsers = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intentUsers);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }

        return true;
    }




}
