package ericli.foodforfriends.activities;


import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.adapters.GroupMessagesAdapter;
import ericli.foodforfriends.models.GroupMessagesModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.ImageRefactor;
import ericli.foodforfriends.utility.Const_and_Methods;

import static ericli.foodforfriends.utility.ImageRefactor.savePhotoImage;
import static ericli.foodforfriends.utility.Const_and_Methods.REQUEST_PICK_IMAGE;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* called when you send a group message
* **/
public class GroupMessagesActivity extends AppCompatActivity implements View.OnClickListener {


    private final int CONST_FOR_SPEECH = 100;


    private ImageButton imgBtnSpeech, imgBtnMessage, imgBtnSend;
    private EditText editMessageView;

    private StorageReference storageReference;


    private String groupid;
    private String uid;

    private DatabaseReference dataReference;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private AlertDialog dialog;

    private final List<GroupMessagesModel> list = new ArrayList<>();

    // adapter
    private GroupMessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        groupid = getIntent().getExtras().getString("groupid");
        dataReference = FirebaseDatabase.getInstance().getReference().child("Group_Chat_Messages").child(groupid);

        storageReference = FirebaseStorage.getInstance().getReference().child("Image_Messages");


        imgBtnSpeech = (ImageButton) findViewById(R.id.chat_speech_btn);
        imgBtnMessage = (ImageButton) findViewById(R.id.chat_send_btn);
        imgBtnSend = (ImageButton) findViewById(R.id.chat_pics_btn);


        editMessageView = (EditText) findViewById(R.id.chat_message_view);
        adapter = new GroupMessagesAdapter(list, GroupMessagesActivity.this);


        recyclerView = (RecyclerView) findViewById(R.id.messages_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        imgBtnSpeech.setOnClickListener(this);
        imgBtnMessage.setOnClickListener(this);
        imgBtnSend.setOnClickListener(this);


        // sets the title to actionbar
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));

        displayMessages();
    }


    /**
     *
     * onclick  for imagebutton send, speechinput, pickimage
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send_btn:
                textMessageSend();
                break;
            case R.id.chat_speech_btn:
                speechInput();
                break;
            case R.id.chat_pics_btn:
                pickImage();
                break;


        }
    }


    /**
     * this method is called to select the image from gallery
     * */
    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }


    /**
     * displays and loads all recently sent messages.
     *
     * */

    private void displayMessages() {
        dataReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Model class referece for each message
                GroupMessagesModel singleMessage = dataSnapshot.getValue(GroupMessagesModel.class);
                // adds the message to message list
                list.add(singleMessage);
                // notifies the adapter each time on child added
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * when you click on speech input it shows the speech input dialog
     * */
    private void speechInput() {


        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        try {
            startActivityForResult(i, CONST_FOR_SPEECH);
        } catch (ActivityNotFoundException excep) {
            Const_and_Methods.Alert(GroupMessagesActivity.this, "Not Supported Speech", "Your Device Not Support Speech Input Method");
        }
    }

    /**
     * this method is used to send the messages inside the group chat
     * */

    private void textMessageSend() {
        // first checks the message
        if (!TextUtils.isEmpty(editMessageView.getText().toString())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", editMessageView.getText().toString());
            map.put("time", ServerValue.TIMESTAMP);
            map.put("uid", uid);

            dataReference.push().setValue(map);
            editMessageView.setText("");

        }


    }

/**
 * used to send images in the groupchat, then stores the image url in firebase
 * */
    private void imageMessageSend(Uri _uri_) {

        final StorageReference imageReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + Calendar.getInstance().getTimeInMillis() + "/" + _uri_.getLastPathSegment());

        UploadTask uploadTask = imageReference.putFile(_uri_);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", editMessageView.getText().toString());
                    map.put("time", ServerValue.TIMESTAMP);
                    map.put("uid", uid);
                    map.put("imageUrl", task.getResult().getDownloadUrl().toString());
                    dataReference.push().setValue(map);
                    editMessageView.setText("");
                    dialog.dismiss();

                } else {
                    dialog.dismiss();

                }
            }
        });

    }
 /*
    * checks if the image is from gallery, also uses speech
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONST_FOR_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    editMessageView.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                }
                break;
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    dialog = new SpotsDialog(GroupMessagesActivity.this, "Sending Image Message ...");
                    dialog.show();

                    Uri _uri = data.getData();
                    Bitmap _bitMap = ImageRefactor.getBitmapForUri(this, _uri);
                    Bitmap _resizeBitMap = ImageRefactor.scaleImage(_bitMap);
                    if (_bitMap != _resizeBitMap)
                        _uri = savePhotoImage(this, _resizeBitMap);
                    imageMessageSend(_uri);
                }
                break;


        }
    }

    /*
    *
    * this method inflates the menu that has searchview to perform the search. also has listener on searchview
    * when you change the text on searchview then the search happens. onQueryTextListener called everytime you change the text inside searchview
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<GroupMessagesModel> newList = new ArrayList<GroupMessagesModel>();

                for (GroupMessagesModel messages : list) {
                    String message = messages.getMessage().toLowerCase();
                    if (message.contains(newText)) {
                        newList.add(messages);
                    }
                }

                adapter.setFilter(newList);
                return true;
            }
        });

        return true;
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
        //finish();
    }


}
