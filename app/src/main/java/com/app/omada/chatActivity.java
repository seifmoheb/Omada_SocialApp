package com.app.omada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class chatActivity extends AppCompatActivity {
    Window window;

    RecyclerView recyclerView;
    private ArrayList<Messages> messagesList = new ArrayList<>();
    MessagesAdapter messagesAdapter;
    private DatabaseReference root;
    String messageSenderId, messageReceiverId, saveCurrentDate, saveCurrentTime;
    TextView receiverName;
    FloatingActionButton send, back;
    EditText textInputEditText;
    ImageView userImage;
    FloatingActionButton sendDoc;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_chat);
        } catch (Exception e) {
            Log.i("Errooor", String.valueOf(e));
        }
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        messagesAdapter = new MessagesAdapter(messagesList);
        sendDoc = findViewById(R.id.sendDoc);
        recyclerView = findViewById(R.id.chatRecycler);
        userImage = findViewById(R.id.imageView16);
        receiverName = findViewById(R.id.receiverName);
        send = findViewById(R.id.sendMessage);
        back = findViewById(R.id.backButton);
        textInputEditText = findViewById(R.id.textInput);
        root = FirebaseDatabase.getInstance().getReference();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try{
        String url;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                messageReceiverId = "";
                url = "";
            } else {
                messageReceiverId = extras.getString("username");
                url = extras.getString("imageURL");
            }
        } else {
            messageReceiverId = (String) savedInstanceState.getSerializable("username");
            url = (String) savedInstanceState.getSerializable("imageURL");

        }
        messageSenderId =  UsersInfo.getUsername();
        messageReceiverId = getIntent().getExtras().get("username").toString();
        if (!url.equals("")) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .apply(requestOptions)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(userImage);
        }
        receiverName.setText(messageReceiverId);
        FetchMessages();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textInputEditText.getText().toString().equals(""))
                    sendMessage();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesAdapter);

        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

        sendDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1);
            }
        });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void FetchMessages() {
        try {
            root.child("Chats").child(UsersInfo.getUsername()).child(messageReceiverId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {

                                Messages messages = dataSnapshot.getValue(Messages.class);

                                messagesList.add(messages);
                                messagesAdapter.notifyDataSetChanged();

                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendMessage() {
        try {
            String message = textInputEditText.getText().toString();
            String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            String message_push_id = databaseReference.getKey();

            Map hashMap = new HashMap<>();
            hashMap.put("sender", messageSenderId);
            hashMap.put("message", message);
            hashMap.put("to", messageReceiverId);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("type", "text");

            Map hashMap2 = new HashMap<>();
            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);


            root.updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    textInputEditText.setText("");
                    sendNotification(messageReceiverId);


                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendNotification(final String messageReceiverId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    send_email = messageReceiverId;

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YTkyYjAwMDktZWJjOC00Mjc2LWEzNjUtNzc2MTQwMTYyNWU5");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"d4f112c6-d699-458e-88ec-0f31783a394f\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"You have got some new messages\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(chatActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Files");
            final String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            final String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = databaseReference.getKey();
            final StorageReference filePath = storageReference.child(message_push_id + "." + "pdf");
            filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(chatActivity.this, "Sending pdf file", Toast.LENGTH_LONG).show();
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            Map hashMap = new HashMap<>();
                            hashMap.put("sender", messageSenderId);
                            hashMap.put("message", downloadUri.toString());
                            hashMap.put("to", messageReceiverId);
                            hashMap.put("date", saveCurrentDate);
                            hashMap.put("time", saveCurrentTime);
                            hashMap.put("type", "pdf");
                            Map hashMap2 = new HashMap<>();
                            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
                            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);
                            root.updateChildren(hashMap2);
                            progressDialog.dismiss();
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    progressDialog.setMessage((int) p + " % Uploading....");
                    progressDialog.setTitle("PDF");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

        }
    }
}