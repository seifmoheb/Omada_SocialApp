package com.app.omada;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class myposts extends AppCompatActivity {

    Window window;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private final ArrayList<String> mImageUrls = new ArrayList<>();
    private final ArrayList<String> mUserImageUrls = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mID = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerMyPostsAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background));
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();
        loadMyData();
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                scrollView.setVisibility(View.INVISIBLE);
                shimmerFrameLayout.setVisibility(VISIBLE);
                shimmerFrameLayout.startShimmer();
                mNames.clear();
                mDescr.clear();
                mImageUrls.clear();
                mUserImageUrls.clear();
                mTime.clear();
                loadMyData();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }
        });
    }
    private void loadMyData(){

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
                        String n = o.getString("username");
                        if(n.equals(nameStr)) {
                            mNames.add("@" + o.getString("username"));
                            mDescr.add(o.getString("descr"));
                            mImageUrls.add(o.getString("photo"));
                            mUserImageUrls.add(o.getString("userImage"));
                            mTime.add(o.getString("time"));
                            mID.add(o.getString("id"));
                        }

                    }
                    adapter = new RecyclerMyPostsAdapter(myposts.this, mNames, mImageUrls, mDescr,mUserImageUrls,mTime,mID);
                    recyclerView.setAdapter(adapter);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myposts.this,error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}