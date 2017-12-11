package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* this activity used to change the name of current signed in user
*
* */
public class ChangeNameActivity extends AppCompatActivity {

    private Button btnChangeName;
    private EditText editName;

    private DatabaseReference dataReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);


        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        editName = (EditText) findViewById(R.id.edit_name);

        editName.setText(getIntent().getExtras().get("name").toString());

        btnChangeName = (Button) findViewById(R.id.btn_changename);

        btnChangeName.setOnClickListener(listener);
    }


    /**
     *
     * When user clicks on change name this is called to change the name of user
     * and this also updates the firebase database
     * */

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!Const_and_Methods.isNetworkAvaliable(ChangeNameActivity.this)) {
                Const_and_Methods.Alert(ChangeNameActivity.this, "No Internet Connection", "Enable Wifi or Mobile Data");
                return;
            }


            final String status = editName.getText().toString();

            if (TextUtils.isEmpty(status)) {
                Const_and_Methods.Alert(ChangeNameActivity.this, "Empty Name", "Enter Your Name First");
            } else {
                final AlertDialog dialog = new SpotsDialog(ChangeNameActivity.this, "Updating Name ... ");
                dialog.show();

                dataReference.child(Const_and_Methods.User_Name).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Const_and_Methods.Alert(ChangeNameActivity.this, "Updated", "Name Updated Successfully");

                        } else {
                            dialog.dismiss();
                            Const_and_Methods.Alert(ChangeNameActivity.this, "Error", "Error while saving Name");
                        }
                    }
                });
            }

        }
    };

    /*
    * finishes the current activity when you press back
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }
    /**
     * creates an animation when you press the back button
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        //  finish();
    }
}
