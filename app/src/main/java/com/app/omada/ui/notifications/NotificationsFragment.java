package com.app.omada.ui.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.omada.Password;
import com.app.omada.R;
import com.app.omada.Settings;
import com.app.omada.UsersInfo;
import com.app.omada.followers;
import com.app.omada.following;
import com.app.omada.info_pop;
import com.app.omada.log;
import com.app.omada.mylikes;
import com.app.omada.myposts;
import com.app.omada.legal_policies;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment  {


    private NotificationsViewModel notificationsViewModel;
    ListView listView;
    TextView textView2,textView4,textView5,textView8,followingText;
    ImageView imageView;
    TextView num1, followersText,num3;
    DatabaseReference rootRef;
    LinearLayout posts,likes,following,contacts;
    String userName;
    FloatingActionButton facebook2;
    Map hashMap;
    int countFollowers,countFollowing;
   public static boolean loggedout;
   AutoCompleteTextView editText;
   Button button;
   TextView visit;
    boolean found = false;
    ProgressDialog progressDialog;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    ArrayList<String> users = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    ArrayAdapter<String> nameAdapter;

    Activity thisActivity;
    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

            notificationsViewModel =
                    ViewModelProviders.of(this).get(NotificationsViewModel.class);
            View root = inflater.inflate(R.layout.fragment_notifications, container, false);
            textView2 = root.findViewById(R.id.textView2);
            textView4 = root.findViewById(R.id.textView4);
            thisActivity = getActivity();
            loadData();
            textView5 = root.findViewById(R.id.textView5);
            visit = root.findViewById(R.id.textView17);
            textView8 = root.findViewById(R.id.textView8);
            followingText = root.findViewById(R.id.followingtext);
            following = root.findViewById(R.id.followinglinear);
            editText = root.findViewById(R.id.search_users);
            button = root.findViewById(R.id.find);
         nameAdapter =  new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,android.R.id.text1,users);
         editText.setAdapter(nameAdapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editText.getText().toString().length()!=0){

                        loadUsers(editText.getText().toString());
                    }
                }
            });
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), following.class);
                    startActivity(intent);
                }
            });
            num1 = root.findViewById(R.id.text5);
        loggedout = false;
            followersText = root.findViewById(R.id.text7);
            num3 = root.findViewById(R.id.text2);
            facebook2 = root.findViewById(R.id.facebook2);

        userName = UsersInfo.getUsername();
        rootRef = FirebaseDatabase.getInstance().getReference("Users").child(userName);
        hashMap = new HashMap<>();


            textView2.setText("@"+userName);
        textView4.setText(UsersInfo.getEmail());

        String[] number= UsersInfo.getPhone().split("_");
        textView5.setText("+"+number[0]);
        imageView = root.findViewById(R.id.profile_image);
        String str = UsersInfo.getImage();
        if(!str.equals("")){
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(getContext())
                    .asBitmap()
                    .load(str)
                    //.apply(requestOptions)
                    .override(150,50)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(imageView);
        }
        if(UsersInfo.getPosts()< 10){
            num1.setText("0"+UsersInfo.getPosts());
        }
        else
            num1.setText(UsersInfo.getPosts());


        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("Likes").child(UsersInfo.getUsername());

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() < 10){
                num3.setText("0"+String.valueOf(snapshot.getChildrenCount()));}
                else{
                    num3.setText(String.valueOf(snapshot.getChildrenCount()));
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            visit.setVisibility(View.INVISIBLE);
            facebook2.setVisibility(View.INVISIBLE);
            textView8.setVisibility(View.VISIBLE);
        textView8.setText(UsersInfo.getSkills());//}
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Following").child(UsersInfo.getUsername());

        countFollowers = 0;
        countFollowing = 0;
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    System.out.println("found this second "+dataSnapshot.child("userFollowed").getValue().toString());
                    if (dataSnapshot.child("userFollowed").getValue().toString().equals(UsersInfo.getUsername())) {
                        countFollowers++;
                        if(countFollowers < 10){
                            followersText.setText("0"+String.valueOf(countFollowers));
                        }
                        else{
                            followersText.setText(String.valueOf(countFollowers));
                        }
                    }
                    else{
                        countFollowing++;
                        if(countFollowing < 10){
                            followingText.setText("0"+String.valueOf(countFollowing));
                        }
                        else{
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





        try {
            listView = root.findViewById(R.id.listView);
            ArrayList<String> items = new ArrayList<String>();
            items.add("Update Profile");
            items.add("Change Password");
            items.add("Legal and policies");
            items.add("Log out");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items){
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.WHITE);
                    return view;
                }
            };
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Intent intent;
                    switch(i){
                        case 0:
                            intent = new Intent(getContext(), Settings.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(getContext(), Password.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(getContext(), legal_policies.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(thisActivity, log.class);
                            final ProgressDialog progressDialog;
                            progressDialog = new ProgressDialog(getContext(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                            progressDialog.setMessage("Loading...");
                            progressDialog.setTitle("Signing you out");
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            try {
                                SQLiteDatabase myDatabase = thisActivity.openOrCreateDatabase("Users",MODE_PRIVATE,null);
                                myDatabase.execSQL("DROP TABLE IF EXISTS usersData");
                                FirebaseAuth.getInstance().signOut();
                                startActivity(intent);
                                loggedout = true;
                                if(thisActivity != null){
                                thisActivity.finish();}
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        posts = root.findViewById(R.id.postslinear);
        contacts = root.findViewById(R.id.contactslinear);
        likes = root.findViewById(R.id.likeslinear);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), myposts.class);
                startActivity(intent);
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), followers.class);
                startActivity(intent);
            }
        });
        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), mylikes.class);
                startActivity(intent);
            }
        });
        return root;

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        String str = UsersInfo.getImage();
        textView4.setText(UsersInfo.getEmail());
        String[] number= UsersInfo.getPhone().split("_");
        textView5.setText("+"+number[0]+" "+number[1]);
        if(UsersInfo.getGender().equals("startup")){

            textView8.setVisibility(View.INVISIBLE);
            if(!UsersInfo.getSkills().equals("")){
                facebook2.setVisibility(View.VISIBLE);
                visit.setVisibility(View.VISIBLE);
                facebook2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UsersInfo.getSkills()));
                        startActivity(intent);
                    }
                });

            }else{
                facebook2.setVisibility(View.GONE);
            }


        }else{
            visit.setVisibility(View.INVISIBLE);
            facebook2.setVisibility(View.INVISIBLE);
            textView8.setVisibility(View.VISIBLE);
            textView8.setText(UsersInfo.getSkills());}
        if(!str.equals("")){

            Glide.with(getContext())
                    .asBitmap()
                    .load(str)
                    //.apply(requestOptions)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(imageView);
        }
    }
    private void loadData(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("users");
                    int count = jsonArray.length();
                    Log.i("FOOUUUNDD",String.valueOf(count));

                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        users.add(o.getString("username"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(thisActivity,"Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(thisActivity);
        requestQueue.add(stringRequest);
    }
    public void loadUsers(final String user){
        progressDialog = new ProgressDialog(getContext(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Searching");
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        final boolean[] found = {false};
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("users");

                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);


                            if(o.getString("username").equals(user)) {
                                found[0] = true;
                                break;
                            }
                            else{
                                found[0] = false;
                            }

                    }
                    if(found[0]){
                        progressDialog.dismiss();
                        info_pop.clicked("@"+editText.getText().toString());
                        Intent intent = (new Intent(getContext(), info_pop.class));
                        getContext().startActivity(intent);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(thisActivity,"Couldn't find this user",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                found[0] = false;
                progressDialog.dismiss();
                Toast.makeText(thisActivity,"Please check your internet connection", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(thisActivity);
        requestQueue.add(stringRequest);



    }
}