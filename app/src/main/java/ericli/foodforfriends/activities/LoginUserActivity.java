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
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * allows users to login using firebase
 * */
public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private EditText editEmail;
    private EditText editPass;

    private DatabaseReference dataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        auth = FirebaseAuth.getInstance();

        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users");

        findViewById(R.id.btn_signin).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);


        editEmail = (EditText) findViewById(R.id.edit_email);
        editPass = (EditText) findViewById(R.id.edit_pass);

        editEmail.addTextChangedListener(new MyTextWatcher(editEmail));
        editPass.addTextChangedListener(new MyTextWatcher(editPass));



    }


    /*
    * checks if the user exists and checks if they're connected to the internet
    * */
    private void Login(String email, String password) {

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (Const_and_Methods.isNetworkAvaliable(LoginUserActivity.this)) {
            final AlertDialog dialog = new SpotsDialog(LoginUserActivity.this, "Login Account...");
            dialog.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String uid = auth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        dataReference.child(uid).child("Device_Token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                startActivity(new Intent(LoginUserActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        });

                    } else {
                        dialog.dismiss();
                        Const_and_Methods.Alert(LoginUserActivity.this, "Wrong Email, Password", "Enter Correct Email, Password!");
                    }

                }
            });

        } else {
            Const_and_Methods.Alert(LoginUserActivity.this, "No Internet Connection", "Enable Wifi or Mobile Data First");

        }
    }


    /*
    *checks to tsee if password and email are legitimate and formatted properly
    *
    * **/

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


    // checks if email is formatted properly

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // verifies email
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


// checks to see if password is valid and is 6 chars
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

    // focuses the keyboard
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }



//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                Login(editEmail.getText().toString(), editPass.getText().toString());
                break;
            case R.id.btn_signup:
                startActivity(new Intent(LoginUserActivity.this, RegisterationActivity.class));
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
        }
    }
}
