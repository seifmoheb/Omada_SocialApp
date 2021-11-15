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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList();
    private ArrayList<String> mUserImages = new ArrayList();
    private ArrayList<String> mTime = new ArrayList();
    private ArrayList<String> mID = new ArrayList();
    private ArrayList<String> likedID = new ArrayList<>();
    String str, str2, str3, str4;
    boolean check = false;
    boolean disable = false;
    Cursor c;
    private Context mContext;


    public RecyclerViewAdapter(Context mcontext, ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mDescr, ArrayList<String> mUserImages, ArrayList<String> mTime, ArrayList<String> mID) {
        this.mImageNames = mImageNames;
        this.mDescr = mDescr;
        this.mImages = mImages;
        this.mContext = mcontext;
        this.mUserImages = mUserImages;
        this.mTime = mTime;
        this.mID = mID;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        try {
            final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Likes").child(UsersInfo.getUsername());

            if (!mImages.get(position).equals("")) {
                holder.imageView.setMinimumHeight(450);
                holder.imageView.setMaxHeight(450);
                Glide.with(mContext)
                        .asBitmap()
                        .load(mImages.get(position))
                        .placeholder(R.color.grayPlaceHolder)
                        .into(holder.imageView);
            }
            if (!mUserImages.get(position).equals("")) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(mUserImages.get(position))
                        .override(150, 50)
                        .placeholder(R.color.grayPlaceHolder)
                        //.apply(requestOptions)
                        .fitCenter()
                        .into(holder.userImageView);
            }

            holder.imageName.setText(mImageNames.get(position));
            holder.imageDescr.setText(mDescr.get(position));

            TimeAgo2 timeAgo2 = new TimeAgo2();
            String MyFinalValue = timeAgo2.covertTimeToText(mTime.get(position));
            holder.time.setText(MyFinalValue);
            String nameStr = "@" + UsersInfo.getUsername();
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        if (dataSnapshot.getValue().toString().equals(mID.get(position)) || dataSnapshot.getValue().toString().equals("{postId=" + mID.get(position) + "}")) {
                            holder.likeButtonInactive.setVisibility(View.INVISIBLE);
                            holder.likeButtonActive.setVisibility(View.VISIBLE);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            root.child(UsersInfo.getUsername()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        if (dataSnapshot.getValue().toString().equals(mID.get(position)) || dataSnapshot.getValue().toString().equals("{postId=" + mID.get(position) + "}")) {
                            holder.likeButtonInactive.setVisibility(View.INVISIBLE);
                            holder.likeButtonActive.setVisibility(View.VISIBLE);
                        }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        if (dataSnapshot.getValue().toString().equals(mID.get(position)) || dataSnapshot.getValue().toString().equals("{postId=" + mID.get(position) + "}")) {
                            holder.likeButtonInactive.setVisibility(View.INVISIBLE);
                            holder.likeButtonActive.setVisibility(View.VISIBLE);
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

            holder.likeButtonInactive.setOnClickListener(new View.OnClickListener() {
                boolean found = false;

                @Override
                public void onClick(View view) {


                    Map hashMap = new HashMap<>();
                    hashMap.put("postId", mID.get(position));
                    root.push().updateChildren(hashMap);
                    holder.likeButtonInactive.setVisibility(View.INVISIBLE);
                    holder.likeButtonActive.setVisibility(View.VISIBLE);
                }
            });

            holder.likeButtonActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child("Likes").child(UsersInfo.getUsername()).orderByChild("postId").equalTo(mID.get(position));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                                holder.likeButtonInactive.setVisibility(View.VISIBLE);
                                holder.likeButtonActive.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                        }
                    });
                }
            });


            if (nameStr.equals(mImageNames.get(position))) {
                holder.submit.setVisibility(View.INVISIBLE);
            }

            holder.submit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    disable = true;
                    String sender = UsersInfo.getUsername();
                    String receiver = mImageNames.get(position).substring(1);
                    Intent intent = new Intent(view.getContext(), chatActivity.class);
                    intent.putExtra("username", receiver);
                    if(mUserImages != null)
                        intent.putExtra("imageURL", mUserImages.get(position));
                    intent.putExtra("sender", UsersInfo.getUsername());
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
        }catch (Exception e){
            e.printStackTrace();
        }

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

class TimeAgo2 {
    public String covertTimeToText(String dataDate) {
        String convTime = null;
        String prefix = "";
        String suffix = "Ago";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();
            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                if(minute == 1){
                    convTime = minute + " Minute " + suffix;}
                else
                    convTime = minute + " Minutes " + suffix;
            } else if (hour < 24) {
                if(hour == 1){

                    convTime = hour + " Hour " + suffix;}
                else
                    convTime = hour + " Hours " + suffix;
        } else if (day >= 7) {
                if (day > 360) {
                    if(day == 1){
                        convTime = (day / 360) + " Year " + suffix;}
                    else{
                        convTime = (day / 360) + " Years " + suffix;
                }
                } else if (day > 30) {
                    if(day == 1){
                        convTime = (day / 30) + " Month " + suffix;}
                    else{
                        convTime = (day / 30) + " Months " + suffix;
                }
                } else {
                    if(day == 1){
                        convTime = (day / 7) + " Week " + suffix;}
                    else{
                        convTime = (day / 7) + " Weeks " + suffix;
                }
                }
            } else if (day < 7) {
                if(day == 1){
                    convTime = day + " Day " + suffix;
                }else
                    convTime = day + " Days " + suffix;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }
        return convTime;
    }
}


