package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.MainActivity;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;

/**
 * Created by ericli on 11/29/2017.
 */
public class RegisterationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editName;
    private EditText editPass;
    private EditText editEmail;

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;


    private FirebaseAuth auth;
    private DatabaseReference dataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = (EditText) findViewById(R.id.edit_name);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPass = (EditText) findViewById(R.id.edit_pass);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);


        editEmail.addTextChangedListener(new MyTextWatcher(editEmail));
        editPass.addTextChangedListener(new MyTextWatcher(editPass));

        findViewById(R.id.btn_loginPage).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

        auth = FirebaseAuth.getInstance();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_loginPage:
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.btn_register:
                RegisterAccount(editEmail.getText().toString(), editPass.getText().toString());
                break;

        }

    }

    private void RegisterAccount(String email, String pass) {
        if (TextUtils.isEmpty(editName.getText().toString())) {
            Const_and_Methods.Alert(RegisterationActivity.this, "Empty Name", "Enter Your Name First");
            return;
        }
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (Const_and_Methods.isNetworkAvaliable(RegisterationActivity.this)) {
            final AlertDialog dialog = new SpotsDialog(RegisterationActivity.this, "Creating new Account...");
            dialog.show();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users").child(auth.getCurrentUser().getUid());

                        dataReference.child(Const_and_Methods.User_Name).setValue(editName.getText().toString());
                        dataReference.child(Const_and_Methods.User_Status).setValue("Hey, I am using Food Friend Chat");
                        dataReference.child(Const_and_Methods.User_Image).setValue("profile_picture");
                        dataReference.child("Device_Token").setValue(deviceToken);
                        dataReference.child(Const_and_Methods.User_thumb_Image).setValue("profile_picture").addOnCompleteListener(new OnCompleteListener<Void>() {


                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    startActivity(new Intent(RegisterationActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();

                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });


                    } else {
                        dialog.dismiss();
                        Const_and_Methods.Alert(RegisterationActivity.this, "Email Error", "Email already exist, use different!");
                    }

                }
            });

        } else {
            Const_and_Methods.Alert(RegisterationActivity.this, "No Internet Connection", "Enable Wifi or Mobile Data First");
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_email:
                    validateEmail();
                    break;
                case R.id.edit_pass:
                    validatePassword();
                    break;

            }
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateEmail() {
        String email = editEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.error_message_email));
            requestFocus(editEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (editPass.getText().toString().trim().isEmpty() || editPass.getText().length() < 6) {
            inputLayoutPassword.setError(getString(R.string.error_message_password));
            requestFocus(editPass);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        // finish();
    }


}
