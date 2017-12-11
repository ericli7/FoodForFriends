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

/**
 * this method changes the status of currently sign in user
 * */
public class ChangeStatusActivity extends AppCompatActivity {

    private Button btnChangeStatus;
    private EditText editStatus;

    private DatabaseReference dataReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);


        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        editStatus = (EditText) findViewById(R.id.edit_status);

        editStatus.setText(getIntent().getExtras().get("status").toString());


        btnChangeStatus = (Button) findViewById(R.id.btn_changestatus);


        btnChangeStatus.setOnClickListener(listener);


    }
    /**
     *
     * When user clicks on changestatus this is called to change the status of user
     * and this also updates the firebase database
     * */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!Const_and_Methods.isNetworkAvaliable(ChangeStatusActivity.this)) {
                Const_and_Methods.Alert(ChangeStatusActivity.this, "No Internet Connection", "Enable Wifi or Mobile Data");
                return;
            }

            final String status = editStatus.getText().toString();

            if (TextUtils.isEmpty(status)) {
                Const_and_Methods.Alert(ChangeStatusActivity.this, "Empty Status", "Enter Your Status First");
            } else {
                final AlertDialog dialog = new SpotsDialog(ChangeStatusActivity.this, "Updating Status ... ");
                dialog.show();

                dataReference.child(Const_and_Methods.User_Status).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();

                            Const_and_Methods.Alert(ChangeStatusActivity.this, "Updated", "Status Updated Successfully");

                        } else {
                            Const_and_Methods.Alert(ChangeStatusActivity.this, "Error", "Error while saving Status");
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
     * creates an animation when you press back
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        //   finish();
    }
}
