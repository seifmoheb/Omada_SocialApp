package com.app.omada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class mylikes extends AppCompatActivity {
    Window window;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private final ArrayList<String> mImageUrls = new ArrayList<>();
    private final ArrayList<String> mUserImageUrls = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mID = new ArrayList<>();
    private ArrayList<String> mIDLiked = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerMyLikesAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylikes);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background));
        scrollView = (NestedScrollView) findViewById(R.id.scrollView2);
        swipeRefreshLayout = findViewById(R.id.swipeContainer2);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(linearLayoutManager);
        shimmerFrameLayout = findViewById(R.id.shimmer2);
        shimmerFrameLayout.startShimmer();
        loadData();
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                scrollView.setVisibility(View.INVISIBLE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                mNames.clear();
                mDescr.clear();
                mImageUrls.clear();
                mUserImageUrls.clear();
                mTime.clear();
                mID.clear();
                mIDLiked.clear();

                loadData();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }
        });
    }
    private void loadData(){

        final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Likes").child(UsersInfo.getUsername());
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mIDLiked.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        root.child(UsersInfo.getUsername()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mIDLiked.add(dataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                }

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
        final String nameStr = UsersInfo.getUsername();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("posts");
                    mNames.clear();
                    mDescr.clear();
                    mImageUrls.clear();
                    mUserImageUrls.clear();
                    mTime.clear();
                    mID.clear();
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);

                        for(int j = 0; j < mIDLiked.size(); j++) {
                            if(("{postId="+o.getString("id")+"}").equals(mIDLiked.get(j))) {
                                Log.i("liked posts done",o.getString("id"));
                                mNames.add("@" + o.getString("username"));
                                mDescr.add(o.getString("descr"));
                                mImageUrls.add(o.getString("photo"));
                                mUserImageUrls.add(o.getString("userImage"));
                                mTime.add(o.getString("time"));
                                mID.add(o.getString("id"));
                            }
                        }
                    }
                    adapter = new RecyclerMyLikesAdapter(getApplicationContext(), mNames, mDescr, mImageUrls,mUserImageUrls,mTime,mID);
                    recyclerView.setAdapter(adapter);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(VISIBLE);
                    swipeRefreshLayout.setEnabled(true);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Likes").child(UsersInfo.getUsername()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(mIDLiked.size() < snapshot.getChildrenCount()){
                                int diff = (int) (snapshot.getChildrenCount() - mIDLiked.size());
                                Snackbar.make(getWindow().getDecorView().getRootView(),diff+" posts you liked have been deleted",Snackbar.LENGTH_INDEFINITE).setAction("Action",null).show();

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mylikes.this,error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}