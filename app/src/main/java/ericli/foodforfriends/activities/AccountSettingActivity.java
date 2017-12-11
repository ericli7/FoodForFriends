package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.Const_and_Methods;
import id.zelory.compressor.Compressor;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* this is activity opens when you click on option menu item "account settings"
* */
public class AccountSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView image;
    private TextView textName, textStatus;
    private DatabaseReference dataReference;
    private FirebaseAuth auth;


    private StorageReference imageStorageReference, thumbImageStorageReferece;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        auth = FirebaseAuth.getInstance();


        imageStorageReference = FirebaseStorage.getInstance().getReference().child("User_Images");
        thumbImageStorageReferece = FirebaseStorage.getInstance().getReference().child("User_Thumbs");


        dataReference = FirebaseDatabase.getInstance().getReference().child("Chat_Users").child(auth.getCurrentUser().getUid());

        dataReference.keepSynced(true);

        textName = (TextView) findViewById(R.id.text_name);
        textStatus = (TextView) findViewById(R.id.text_status);
        image = (CircleImageView) findViewById(R.id.image);

        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_status).setOnClickListener(this);
        findViewById(R.id.btn_name_change).setOnClickListener(this);


        /**
         *
         * value event listener on chatusers to load image, name and status that with you are  currently login
         */

        dataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Const_and_Methods.User_Name).getValue().toString();
                textName.setText(name);

                String status = dataSnapshot.child(Const_and_Methods.User_Status).getValue().toString();
                textStatus.setText(status);

                final String image = dataSnapshot.child(Const_and_Methods.User_Image).getValue().toString();


                if (!image.equals("profile_picture")) {

                    Picasso.with(AccountSettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_profile).into(AccountSettingActivity.this.image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(AccountSettingActivity.this).load(image).placeholder(R.drawable.user_profile).into(AccountSettingActivity.this.image);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
    * override method ... this method is called when back pressed from android phone
    * */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
        // finish();
    }


    private static final int GALLERY_PICK = 1;

    /*
    *  onClick method has 3 button click cases when you click on change status, click on change name and also listener
    *  change image
    * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_name_change:
                startActivity(new Intent(AccountSettingActivity.this, ChangeNameActivity.class).putExtra("name", textName.getText().toString()));
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.btn_status:
                startActivity(new Intent(AccountSettingActivity.this, ChangeStatusActivity.class).putExtra("status", textStatus.getText().toString()));
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.btn_image:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
                break;

        }
    }

    /*
    * Activity result method checks whether the image is pick from gallery if yes then crop the image and when
    * image cropped successfully then we upload this to firebase storage and also update database to save downloaded image url
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);


        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final AlertDialog dialog = new SpotsDialog(AccountSettingActivity.this, "Wait Until to Upload Image ...");
                dialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath_Uri = new File(resultUri.getPath());

                String user_uid = auth.getCurrentUser().getUid();


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_filePath_Uri);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = imageStorageReference.child(user_uid + ".jpg");
                final StorageReference thumb_filepath = thumbImageStorageReferece.child(user_uid + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put(Const_and_Methods.User_Image, download_url);
                                        update_hashMap.put(Const_and_Methods.User_thumb_Image, thumb_downloadUrl);
                                        dataReference.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    dialog.dismiss();
                                                    Const_and_Methods.Alert(AccountSettingActivity.this, "Successfully Uploaded", "Your Image Successfully Uplaoded!");

                                                }

                                            }
                                        });


                                    } else {

                                        Const_and_Methods.Alert(AccountSettingActivity.this, "Email uploading", "Error in uploading thumbnail!");
                                        dialog.dismiss();

                                    }

                                }
                            });


                        } else {

                            Const_and_Methods.Alert(AccountSettingActivity.this, "Error Uploading", "Error while uploading image, try again!");

                            dialog.dismiss();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("Error........",result.getError().toString());


            }
        }

    }


}
