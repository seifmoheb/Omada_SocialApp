package com.app.omada.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.app.omada.CustomJsonObjectRequest;
import com.app.omada.R;
import com.app.omada.RecyclerMessageAdapter;
import com.app.omada.UsersInfo;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DashboardFragment extends Fragment {


        Activity thisActivity;
        private DashboardViewModel dashboardViewModel;
        private static final String URL_DATA = "https://localdata.000webhostapp.com/db.php";
        private ArrayList<String> mNames = new ArrayList<>();
        private ArrayList<String> lastMessage = new ArrayList<>();
        Map<String,Date> hash = new TreeMap<>();
        Map<String,String> hashLastMessage = new TreeMap<>();
        private ArrayList<String> mImageUrls = new ArrayList<>();
        RecyclerView recyclerView;
        RecyclerMessageAdapter adapter;
        LinearLayoutManager linearLayoutManager;
        String r;
        String messageSenderId;
        private DatabaseReference rootRef;
        int count = 0;
        Context context;
        boolean bool = false;
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            dashboardViewModel =
                    ViewModelProviders.of(this).get(DashboardViewModel.class);
            View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rootRef = FirebaseDatabase.getInstance().getReference();
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);
            context = getActivity();
            recyclerView = root.findViewById(R.id.recyclerViewMessages);
            recyclerView.setLayoutManager(linearLayoutManager);
            thisActivity = getActivity();


            messageSenderId = UsersInfo.getUsername();

            try {


                    System.out.println("internet");
                    if (mNames == null) {
                    }
                    if (mImageUrls == null) {
                    }
                    if (lastMessage == null) {
                    }
                    //sharedPreferences.edit().clear().apply();
                    // readmessages();
                    DatabaseReference db;
                    db = FirebaseDatabase.getInstance().getReference().child("Chats").child(messageSenderId);
                    db.keepSynced(true);
                    db.orderByKey().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            refresh();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            refresh();

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
                    recyclerView.setAdapter(adapter);

            }catch(Exception e){
                e.printStackTrace();
            }
            return root;
        }
        @Override
        public void onPause() {
            super.onPause();
        }

        public void refresh(){
            hash.clear();
            lastMessage.clear();
            mNames.clear();
            mImageUrls.clear();
            final DatabaseReference databaseReference;

            databaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(messageSenderId);
            databaseReference.keepSynced(true);

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {

                    if(dataSnapshot.exists()){
                        final String sender = dataSnapshot.getKey();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Chats").child(messageSenderId).child(sender);
                        Query query = db.orderByKey().limitToLast(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                count = (int)snapshot.getChildrenCount();

                                for (DataSnapshot child : snapshot.getChildren()){
                                    System.out.println(child.getKey());
                                    System.out.println();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                                    try {
                                        System.out.println(sender+dateFormat.parse(child.child("time").getValue().toString()+" "+child.child("date").getValue().toString()));

                                        hash.put(sender,dateFormat.parse(child.child("time").getValue().toString()+" "+child.child("date").getValue().toString()));
                                        if(child.child("type").getValue().toString().equals("text")) {
                                            hashLastMessage.put(sender, child.child("message").getValue().toString());
                                        }else{
                                            hashLastMessage.put(sender, "PDF");
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                                if(hash.size() == count) {
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // count += (int)snapshot.getChildrenCount();


                                            Map sortedMap = TreeMapDemo.sortByValues(hash);
                                            Set sortedSet = sortedMap.entrySet();
                                            Iterator i = sortedSet.iterator();
                                            while (i.hasNext()) {
                                                Map.Entry me = (Map.Entry) i.next();
                                                mNames.add(me.getKey().toString());
                                                lastMessage.add(hashLastMessage.get(me.getKey().toString()));
                                                if (!(i.hasNext())) {

                                                    CustomJsonObjectRequest rq = new CustomJsonObjectRequest(Request.Method.GET,
                                                            URL_DATA, null, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                JSONArray jsonArray = jsonObject.getJSONArray("users");
                                                                for (int j = 0; j < mNames.size(); j++) {

                                                                    for (int k = 0; k < jsonArray.length(); k++) {
                                                                        JSONObject o = jsonArray.getJSONObject(k);
                                                                        if (o.getString("username").equals(mNames.get(j))) {
                                                                            mImageUrls.add(o.getString("image"));
                                                                        }
                                                                    }
                                                                }
                                                                adapter = new RecyclerMessageAdapter(getContext(), mNames, mImageUrls, lastMessage);
                                                                recyclerView.setAdapter(adapter);
                                                                adapter.notifyDataSetChanged();

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }},
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    if(thisActivity != null)
                                                                        Toast.makeText(thisActivity,"Failed to load",Toast.LENGTH_LONG).show();
                                                                }
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
                                                    RequestQueue requestQueue = Volley.newRequestQueue(thisActivity);
                                                    requestQueue.add(rq);
                                                }
                                                System.out.print(me.getKey() + ": ");
                                                System.out.println(me.getValue());
                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



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
        }

        @Override
        public void onResume() {
            super.onResume();



        }

    }
class TreeMapDemo {
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator = new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k1).compareTo(map.get(k2));
                if (compare == 0)
                    return 1;
                else
                    return compare;
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}


