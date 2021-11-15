package com.app.omada;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerMyLikesAdapter extends RecyclerView.Adapter<RecyclerMyLikesAdapter.ViewHolder> {
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList();
    private ArrayList<String> mUserImages = new ArrayList();
    private ArrayList<String> mTime = new ArrayList();
    private ArrayList<String> mID = new ArrayList();
    private ArrayList<String>IDLiked = new ArrayList<>();
    Cursor c;
    private Context mContext;

    public RecyclerMyLikesAdapter( Context mContext, ArrayList<String> mImageNames, ArrayList<String> mDescr, ArrayList<String> mImages, ArrayList<String> mUserImages, ArrayList<String> mTime, ArrayList<String> mID) {
        this.mImageNames = mImageNames;
        this.mDescr = mDescr;
        this.mImages = mImages;
        this.mUserImages = mUserImages;
        this.mTime = mTime;
        this.mID = mID;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        holder.likeButtonInactive.setVisibility(View.INVISIBLE);
        holder.likeButtonActive.setVisibility(View.VISIBLE);

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


        String nameStr = "@"+UsersInfo.getUsername();



        holder.likeButtonActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("Likes").child(UsersInfo.getUsername()).orderByChild("postId").equalTo(mID.get(position));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            holder.likeButtonInactive.setVisibility(View.VISIBLE);
                            holder.likeButtonActive.setVisibility(View.INVISIBLE);
                            mImageNames.remove(position);
                            mDescr.remove(position);
                            mID.remove(position);
                            mImages.remove(position);
                            mUserImages.remove(position);
                            mTime.remove(position);
                            RecyclerMyLikesAdapter.this.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                    }
                });
            }
        });




        holder.submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                c.moveToFirst();
                int nameIndex = c.getColumnIndex("name");
                String sender = c.getString(nameIndex);
                String receiver = mImageNames.get(position).substring(1);
                Intent intent = new Intent(view.getContext(), chatActivity.class);
                intent.putExtra("username",receiver);
                intent.putExtra("imageURL",mUserImages.get(position));
                view.getContext().startActivity(intent);
            }
        });
        holder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                info_pop.clicked(mImageNames.get(position));
                Intent intent = (new Intent(view.getContext(), info_pop.class));
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDescr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageName;
        TextView imageDescr;
        TextView time;
        ImageView userImageView;
        Button submit, likeButtonInactive, likeButtonActive;
        LinearLayout parentLayout, userInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            imageDescr = itemView.findViewById(R.id.imageDescr);
            time = itemView.findViewById(R.id.time);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            userInfo = itemView.findViewById(R.id.userInfo);
            userImageView = itemView.findViewById(R.id.profile_image_recycler);
            submit = itemView.findViewById(R.id.button5);
            likeButtonInactive = itemView.findViewById(R.id.LikeButtonInactive);
            likeButtonActive = itemView.findViewById(R.id.LikeButtonActive);
        }
    }
}

