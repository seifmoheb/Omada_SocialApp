package com.app.omada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class followers extends AppCompatActivity {

    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    Window window;
    private ArrayList<String> mNames = new ArrayList<>();
    private final ArrayList<String> mUserImageUrls = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerFollowerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    int count = 0;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background));
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.recyclerView_followed);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(followers.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Followers");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadData();
    }
    private void loadData(){

        count = 0;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Following").child(UsersInfo.getUsername());
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("found this first "+snapshot.getValue().toString());


                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {


                    if (dataSnapshot.child("userFollowed").getValue().toString().equals(UsersInfo.getUsername())) {
                        count++;
                        if(count >= dataSnapshot.getChildrenCount()){

                        }
                        mNames.add(dataSnapshot.child("userFollowing").getValue().toString());
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("users");

                    mUserImageUrls.clear();
                    System.out.println("numberrrrr0 "+mNames.size());
                    for(int j = 0; j < mNames.size(); j++) {
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);


                            if(o.getString("username").equals(mNames.get(j))) {
                                mUserImageUrls.add(o.getString("image"));
                            }
                        }
                    }
                    System.out.println("numberrrrr1 "+mNames.size());
                    System.out.println("numberrrrr2 "+mUserImageUrls.size());
                    adapter = new RecyclerFollowerAdapter(getApplicationContext(), mNames,mUserImageUrls);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(followers.this,"Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}