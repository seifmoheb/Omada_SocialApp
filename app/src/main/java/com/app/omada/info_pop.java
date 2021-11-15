package com.app.omada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class info_pop extends AppCompatActivity {
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    String email, phone, gender, skills, image;
    int posts;
    ArrayList<String> users = new ArrayList<>();
    TextView textView2, textView4, textView5, textView8;
    ImageView imageView2;
    static String str, str2, str3, str4, str5, str6;
    FloatingActionButton back;
    boolean check = false;
    TextView num1, followersText, followingText, num3;
    FloatingActionButton facebook, add, remove;
    boolean checkFound = false;
    int countFollowers, countFollowing;

    String nameStr, nameStr2;
    TextView visit;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pop);
        textView2 = findViewById(R.id.textView2_pop);
        textView4 = findViewById(R.id.textView4_pop);
        textView5 = findViewById(R.id.textView5_pop);
        textView8 = findViewById(R.id.textView8_pop);
        imageView2 = findViewById(R.id.profile_image_pop);
        back = findViewById(R.id.button9);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        num1 = findViewById(R.id.text5);
        visit = findViewById(R.id.textView18);
        followersText = findViewById(R.id.text7);
        followingText = findViewById(R.id.followingtext);
        num3 = findViewById(R.id.text2);
        facebook = findViewById(R.id.facebook2);
        add = findViewById(R.id.add_button);
        remove = findViewById(R.id.remove_button);
        nameStr = UsersInfo.getUsername();
        add.setVisibility(View.INVISIBLE);
        remove.setVisibility(View.INVISIBLE);
        loadData();
    }


    public void loadData() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    int count = jsonArray.length();
                    check = true;


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        users.add(o.getString("username"));
                        String name = "@" + o.getString("username");
                        nameStr2 = o.getString("username");
                        users.add(o.getString("updates"));
                        email = o.getString("email");
                        users.add(email);
                        phone = o.getString("phone");
                        users.add("+" + phone);
                        gender = o.getString("gender");
                        users.add(gender);
                        skills = o.getString("skills");
                        users.add(skills);
                        image = o.getString("image");
                        users.add(image);
                        posts = o.getInt("posts");
                        users.add(String.valueOf(posts));
                        if (name.equals(str)) {

                            str2 = email;

                            str3 = phone;
                                str4 = skills;
                                textView8.setVisibility(View.VISIBLE);
                                textView8.setText(str4);
                            str5 = image;
                            str6 = String.valueOf(posts);
                            textView2.setText(str);
                            textView4.setText(str2);
                            String[] number = str3.split("_");
                            textView5.setText("+" + number[0] + " " + number[1]);

                            if (!str5.equals("")) {

                                RequestOptions requestOptions = new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true);
                                Glide.with(info_pop.this)
                                        .asBitmap()
                                        .load(str5)
                                        .fitCenter()
                                        //.apply(requestOptions)
                                        .override(300, 100)
                                        .placeholder(R.color.grayPlaceHolder)
                                        .into(imageView2);
                            }
                            if (Integer.parseInt(str6) < 10)
                                num1.setText("0" + str6);
                            else
                                num1.setText(str6);
                            final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes").child(o.getString("username"));

                            likesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount() < 10) {
                                        num3.setText("0" + String.valueOf(snapshot.getChildrenCount()));
                                    } else {
                                        num3.setText(String.valueOf(snapshot.getChildrenCount()));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            if (nameStr.equals(nameStr2)) {
                                add.setVisibility(View.INVISIBLE);
                                remove.setVisibility(View.INVISIBLE);
                                System.out.println("here is " + nameStr2 + " = " + nameStr);


                            } else {
                                add.setVisibility(View.VISIBLE);
                                System.out.println("here is " + nameStr2 + " != " + nameStr);

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Following").child(nameStr);
                                db.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        System.out.println("found this first " + snapshot.getValue().toString());


                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            System.out.println(nameStr2);
                                            if (dataSnapshot.child("userFollowed").getValue().toString().equals(nameStr2)) {
                                                remove.setVisibility(View.VISIBLE);
                                                add.setVisibility(View.INVISIBLE);
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
                                add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Following").child(nameStr).child(nameStr2).push();
                                        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Following").child(nameStr2).child(nameStr).push();

                                        String messagesender_ref = "Following/" + nameStr + "/" + nameStr2;
                                        String messagereceiver_ref = "Following/" + nameStr2 + "/" + nameStr;
                                        String message_push_id = db.getKey();

                                        Map hash = new HashMap();
                                        hash.put("userFollowed", nameStr2);
                                        hash.put("userFollowing", nameStr);
                                        Map hash2 = new HashMap();
                                        hash2.put("userFollowed", nameStr2);
                                        hash2.put("userFollowing", nameStr);
                                        db2.updateChildren(hash2);

                                        db.updateChildren(hash).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                add.setVisibility(View.INVISIBLE);
                                                remove.setVisibility(View.VISIBLE);
                                                Toast.makeText(getApplicationContext(), "You are now following " + nameStr2, Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                });
                                remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        Query query = ref.child("Following").child(nameStr).child(nameStr2).orderByChild("userFollowed").equalTo(nameStr2);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    snapshot.getRef().removeValue();
                                                    add.setVisibility(View.VISIBLE);
                                                    remove.setVisibility(View.INVISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                                            }
                                        });
                                        Query query2 = ref.child("Following").child(nameStr2).child(nameStr).orderByChild("userFollowing").equalTo(nameStr);

                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    snapshot.getRef().removeValue();
                                                    add.setVisibility(View.VISIBLE);
                                                    remove.setVisibility(View.INVISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                                            }
                                        });
                                    }
                                });
                            }
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Following").child(nameStr2);

                            countFollowers = 0;
                            countFollowing = 0;
                            databaseReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        System.out.println("found this second " + dataSnapshot.child("userFollowed").getValue().toString());
                                        System.out.println("found this second " + dataSnapshot.child("userFollowing").getValue().toString());

                                        if (dataSnapshot.child("userFollowed").getValue().toString().equals(nameStr2)) {
                                            countFollowers++;
                                            if (countFollowers < 10) {
                                                followersText.setText("0" + String.valueOf(countFollowers));
                                            } else {
                                                followersText.setText(String.valueOf(countFollowers));
                                            }
                                        } else {
                                            countFollowing++;
                                            if (countFollowing < 10) {
                                                followingText.setText("0" + String.valueOf(countFollowing));
                                            } else {
                                                followingText.setText(String.valueOf(countFollowing));
                                            }
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

                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
                Log.i("FOOUUUNDD", error.getMessage());

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public static void clicked(String user) {
        str = user;
    }
}
