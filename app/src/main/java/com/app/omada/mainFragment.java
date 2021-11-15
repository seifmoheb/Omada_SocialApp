package com.app.omada;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class mainFragment extends Fragment {
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private final ArrayList<String> mImageUrls = new ArrayList<>();
    private final ArrayList<String> mUserImageUrls = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mID = new ArrayList<>();
    EditText search;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    FloatingActionButton floatingActionButton;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView scrollView;
    Cache cache;
    Network network;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        search = root.findViewById(R.id.search3);
        scrollView = (NestedScrollView) root.findViewById(R.id.scrollView);

        swipeRefreshLayout = root.findViewById(R.id.swipeContainer);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        shimmerFrameLayout = root.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();
        loadData();



        search = root.findViewById(R.id.search3);


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
                search.setText("");

                loadData();

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }
        });
        search.addTextChangedListener(textWatcher);

        return root;
    }
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged( CharSequence charSequence, int i, int i1, int i2) {


            if (charSequence.length() > 0){

                scrollView.setVisibility(View.INVISIBLE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                mNames.clear();
                mDescr.clear();
                mImageUrls.clear();
                mUserImageUrls.clear();
                mTime.clear();
                loadDataFiltered();

            }
            else
                loadData();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private void loadDataFiltered(){


        cache = new DiskBasedCache(getContext().getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        final String[]skills = search.getText().toString().split(" ");
        CustomJsonObjectRequest rq = new CustomJsonObjectRequest(Request.Method.GET,
                URL_DATA, null, new Response.Listener<String>() {
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
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        String descr = o.getString("descr");
                        String d[] = descr.split(" ");
                        boolean check = false;
                        String n = o.getString("username");
                        for (int j = 0; j < skills.length; j++) {
                            System.out.println(skills[j]);

                            for (int k = 0; k < d.length; k++) {
                                System.out.println(d[k]);

                                if (d[k].toLowerCase().contains(skills[j].toLowerCase())) {
                                    System.out.println("true");
                                    mNames.add("@" + o.getString("username"));
                                    mDescr.add(o.getString("descr"));
                                    mImageUrls.add(o.getString("photo"));
                                    mUserImageUrls.add(o.getString("userImage"));
                                    mTime.add(o.getString("time"));
                                    mID.add(o.getString("id"));
                                    check = true;
                                    break;
                                }

                            }
                            if (check == true)
                                break;

                        }



                    }
                    adapter = new RecyclerViewAdapter(getContext(), mNames, mImageUrls, mDescr,mUserImageUrls,mTime,mID);
                    recyclerView.setAdapter(adapter);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.INVISIBLE);
                    scrollView.setVisibility(VISIBLE);
                    swipeRefreshLayout.setEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Request added to the RequestQueue
        RequestQueue requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        VolleyController.getInstance(getActivity()).addToRequestQueue(rq);


    }
    private void loadData(){

        cache = new DiskBasedCache(getContext().getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        CustomJsonObjectRequest rq = new CustomJsonObjectRequest(Request.Method.GET,
                URL_DATA, null, new Response.Listener<String>() {
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
                                mNames.add("@" + o.getString("username"));
                                mDescr.add(o.getString("descr"));
                                mImageUrls.add(o.getString("photo"));
                                mUserImageUrls.add(o.getString("userImage"));
                                mTime.add(o.getString("time"));
                                mID.add(o.getString("id"));
                            }
                            adapter = new RecyclerViewAdapter(getContext(), mNames, mImageUrls, mDescr,mUserImageUrls,mTime,mID);
                            recyclerView.setAdapter(adapter);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            scrollView.setVisibility(VISIBLE);
                            swipeRefreshLayout.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Request added to the RequestQueue
        RequestQueue requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        VolleyController.getInstance(getActivity()).addToRequestQueue(rq);
    }

}
