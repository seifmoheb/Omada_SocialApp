package com.app.omada;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerMyPostsAdapter extends RecyclerView.Adapter<RecyclerMyPostsAdapter.ViewHolderPosts>{

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList();
    private ArrayList<String> mUserImages = new ArrayList();
    private ArrayList<String> mTime = new ArrayList();
    private ArrayList<String> mID = new ArrayList();
    Cursor c;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private static final String URL_DATA_2 = "HOST_LINK_GOES_HERE";
    int postNum;
    private Context mContext;
    public RecyclerMyPostsAdapter(Context mcontext,ArrayList<String> mImageNames , ArrayList<String> mImages, ArrayList<String> mDescr, ArrayList<String> mUserImages, ArrayList<String> mTime, ArrayList<String> mID) {
        this.mImageNames = mImageNames;
        this.mDescr = mDescr;
        this.mImages = mImages;
        this.mUserImages = mUserImages;
        this.mTime = mTime;
        this.mID = mID;
        this.mContext = mcontext;
    }

    @NonNull
    @Override
    public ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_2, parent,false);
        RecyclerMyPostsAdapter.ViewHolderPosts holder = new RecyclerMyPostsAdapter.ViewHolderPosts(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPosts holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        postNum = UsersInfo.getPosts();
        if(!mImages.get(position).equals("")){
            holder.imageView.setMinimumHeight(450);
            holder.imageView.setMaxHeight(450);
            Glide.with(mContext)
                    .asBitmap()
                    .load(mImages.get(position))
                    //.apply(requestOptions)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(holder.imageView);
        }
        if(!mUserImages.get(position).equals("")){
            Glide.with(mContext)
                    .asBitmap()
                    .load(mUserImages.get(position))
                    //.apply(requestOptions)
                    .override(150,50)
                    .fitCenter()
                    .placeholder(R.color.grayPlaceHolder)
                    .into(holder.userImageView);
        }
        else{

        }

        holder.imageName.setText(mImageNames.get(position));
        holder.imageDescr.setText(mDescr.get(position));
        TimeAgo2 timeAgo2 = new TimeAgo2();
        String MyFinalValue = timeAgo2.covertTimeToText(mTime.get(position));
        holder.time.setText(MyFinalValue);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
               final AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(view.getContext(),R.style.Theme_MaterialComponents_Dialog);
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(view.getContext(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                progressDialog.setMessage("Loading...");
                progressDialog.setTitle("Deleting");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("Failure")){
                                Toast.makeText(view.getContext(), response, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                alertDialog.setMessage("Failed to delete post");
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((myposts)mContext).finish();
                                    }
                                });
                                AlertDialog alertDialog1 = alertDialog.create();
                                alertDialog1.show();

                            }
                            else {
                                mID.remove(position);
                                mDescr.remove(position);
                                mImageNames.remove(position);
                                mImages.remove(position);
                                mUserImages.remove(position);
                                mTime.remove(position);
                                update(UsersInfo.getUsername(),view, String.valueOf(UsersInfo.getPosts()-1));
                                UsersInfo.setPosts(postNum);
                                SQLiteDatabase myDatabase = view.getContext().openOrCreateDatabase("Users",MODE_PRIVATE,null);
                                myDatabase.execSQL("UPDATE usersData SET posts = "+UsersInfo.getPosts()+"");
                                progressDialog.dismiss();
                                alertDialog.setMessage("Successfully deleted!");
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((myposts)mContext).finish();
                                    }
                                });
                                AlertDialog alertDialog1 = alertDialog.create();
                                alertDialog1.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        alertDialog.setMessage("Failed to delete!");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((myposts)mContext).finish();
                            }
                        });
                        AlertDialog alertDialog1 = alertDialog.create();
                        alertDialog1.show();

                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", mID.get(position));
                        if(mImages.get(position) != null){
                            params.put("image","found");
                        }
                        params.put("name",UsersInfo.getUsername());
                        postNum = UsersInfo.getPosts();
                        postNum--;
                        params.put("posts",String.valueOf(postNum));
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                requestQueue.add(stringRequest);

            }
        });
        String nameStr = "@"+UsersInfo.getUsername();

    }

    @Override
    public int getItemCount() {
        return mDescr.size();
    }
    public class ViewHolderPosts extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView imageName;
        TextView imageDescr;
        TextView time;
        ImageView userImageView;
        LinearLayout parentLayout,userInfo;
        Button delete;

        public  ViewHolderPosts(@NonNull View itemView){
            super(itemView);
            imageView  = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            imageDescr = itemView.findViewById(R.id.imageDescr);
            time = itemView.findViewById(R.id.time);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            userInfo = itemView.findViewById(R.id.userInfo);
            userImageView = itemView.findViewById(R.id.profile_image_recycler);
            delete = itemView.findViewById(R.id.delete);
        }

    }
    private void update(final String name, final View view, final String p){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                postNum--;
                params.put("posts", p);
                params.put("totalposts", "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
    }

