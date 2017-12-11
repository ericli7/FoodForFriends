package ericli.foodforfriends.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import ericli.foodforfriends.adapters.MessagesAdapter;
import ericli.foodforfriends.utility.IRecyclerViewCallbacks;
import ericli.foodforfriends.models.ChatMessageModel;
import ericli.foodforfriends.R;
import ericli.foodforfriends.utility.ImageRefactor;
import ericli.foodforfriends.utility.Const_and_Methods;

import static ericli.foodforfriends.utility.ImageRefactor.savePhotoImage;
import static ericli.foodforfriends.utility.Const_and_Methods.REQUEST_PICK_IMAGE;

/**
 * Created by ericli on 11/29/2017.
 */

/*
* sends messages to your friends and shows messages
*
* */
public class MessagesActivity extends AppCompatActivity {


    private String chatuserUID, uid;

    private DatabaseReference rootReference;

    StorageReference storageReference;

    private AlertDialog dialog;
    private TextView textTitle;
    private TextView textLastSeen;
    private CircleImageView image;
    private FirebaseAuth auth;


    private ImageButton imgBtnSpeech;
    private ImageButton imgBtnSend;
    private ImageButton imgBtnPickImage;

    private EditText editMessage;

    private RecyclerView recyclerView;

    private final List<ChatMessageModel> list = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessagesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        rootReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();


        storageReference = FirebaseStorage.getInstance().getReference().child("Image_Messages");

        chatuserUID = getIntent().getStringExtra("user_id");

        final String userName = getIntent().getStringExtra("user_name");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_appbar, null);

        actionBar.setCustomView(action_bar_view);


        textTitle = (TextView) findViewById(R.id.custom_bar_title);
        textLastSeen = (TextView) findViewById(R.id.custom_bar_seen);
        image = (CircleImageView) findViewById(R.id.custom_bar_image);
        imgBtnSpeech = (ImageButton) findViewById(R.id.chat_speech_btn);
        imgBtnSend = (ImageButton) findViewById(R.id.chat_send_btn);
        editMessage = (EditText) findViewById(R.id.chat_message_view);
        imgBtnPickImage = (ImageButton) findViewById(R.id.chat_pics_btn);


        /*
        *
        * deletes messages from firebase by setting setValue(null) to remove
        */
        adapter = new MessagesAdapter(list, MessagesActivity.this, new IRecyclerViewCallbacks() {

            @Override
            public void onClick(Object object, final int position) {
                final AlertDialog dialog = new SpotsDialog(MessagesActivity.this, "Deleting Message...");
                dialog.show();

                final String deleteKey = (String) object;

                rootReference.child("messages").child(uid).child(chatuserUID).child(deleteKey).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            rootReference.child("messages").child(chatuserUID).child(uid).child(deleteKey).setValue(null);
                            dialog.dismiss();
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });


            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.messages_list);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        displayMessages();

        textTitle.setText(userName);


        /**
         * loads the online status image and name that show on appbar
         */

        rootReference.child("Chat_Users").child(chatuserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child(Const_and_Methods.User_thumb_Image).getValue().toString();

                Picasso.with(MessagesActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(MessagesActivity.this.image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(MessagesActivity.this).load(image).placeholder(R.drawable.user_profile).into(MessagesActivity.this.image);
                    }
                });

                if (online.equals("true")) {

                    textLastSeen.setText("Online");


                } else {


                    long lastTime = Long.parseLong(online);


                    textLastSeen.setText(Const_and_Methods.getTimeAgo(lastTime, MessagesActivity.this));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        rootReference.child("Chat").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(chatuserUID)) {

                    Map map = new HashMap();
                    map.put("seen", false);
                    map.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + uid + "/" + chatuserUID, map);
                    chatUserMap.put("Chat/" + chatuserUID + "/" + uid, map);

                    rootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                               //checks error
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendTextMessage();

            }
        });

        imgBtnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        imgBtnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechInput();
            }
        });

    }


    /**
     * when you click imagebutton this method is called to pick the image from gallery
     */

    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }


    private final int REQ_CODE_SPEECH_INPUT = 100;

    /**
     * when you click on speech input it show the speech input dialog
     */

    private void speechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this method is used to send image as a messasge inside group chat. When you send images, firebase storage saves the images and downloads the url
     * saved inside firebase database for future use
     */


    private void imageMessageSend(Uri _uri_) {

        final StorageReference imageReference = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + Calendar.getInstance().getTimeInMillis() + "/" + _uri_.getLastPathSegment());

        UploadTask uploadTask = imageReference.putFile(_uri_);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    String message = editMessage.getText().toString();

                    String current_user_ref = "messages/" + uid + "/" + chatuserUID;
                    String chat_user_ref = "messages/" + chatuserUID + "/" + uid;
                    DatabaseReference user_message_push = rootReference.child("messages")
                            .child(uid).child(chatuserUID).push();
                    String pushId = user_message_push.getKey();
                    Map map = new HashMap();

                    map.put("message", message);
                    map.put("from", uid);
                    map.put("seen", false);
                    map.put("type", "text");
                    map.put("deletekey", pushId);
                    map.put("time", ServerValue.TIMESTAMP);
                    map.put("imageUrl", task.getResult().getDownloadUrl().toString());


                    Map map1 = new HashMap();
                    map1.put(current_user_ref + "/" + pushId, map);
                    map1.put(chat_user_ref + "/" + pushId, map);

                    editMessage.setText("");

                    rootReference.updateChildren(map1, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.e("error......", databaseError.getMessage());

                            }
                        }
                    });


                    dialog.dismiss();

                } else {
                    dialog.dismiss();

                }
            }
        });

    }
 /*
    * checks if the image is from gallery and also if the speech is done properly
    * */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editMessage.setText(result.get(0).toString());

                }

                break;
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    dialog = new SpotsDialog(MessagesActivity.this, "Sending Image Message ...");
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

    /**
     * displays all messages in the activity
     */

    private void displayMessages() {


        rootReference.child("messages").child(uid).child(chatuserUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessageModel message = dataSnapshot.getValue(ChatMessageModel.class);
                list.add(message);
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
     * this method is used to send the text message
     */

    private void sendTextMessage() {

        String message = editMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + uid + "/" + chatuserUID;
            String chat_user_ref = "messages/" + chatuserUID + "/" + uid;
            DatabaseReference user_message_push = rootReference.child("messages")
                    .child(uid).child(chatuserUID).push();
            String pushId = user_message_push.getKey();
            Map map = new HashMap();

            map.put("message", message);
            map.put("from", uid);
            map.put("seen", false);
            map.put("type", "text");
            map.put("deletekey", pushId);
            map.put("time", ServerValue.TIMESTAMP);


            Map map1 = new HashMap();
            map1.put(current_user_ref + "/" + pushId, map);
            map1.put(chat_user_ref + "/" + pushId, map);

            editMessage.setText("");

            rootReference.updateChildren(map1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.e("error......", databaseError.getMessage());

                    }
                }
            });


        }
    }

    /*
  *
  * this override method is used to inflate the menu that has searchview. onQueryTextListener is called every time the text is changed inside searchview
  * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
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
                ArrayList<ChatMessageModel> newList = new ArrayList<ChatMessageModel>();

                for (ChatMessageModel messages : list) {
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
        //  finish();
    }

}