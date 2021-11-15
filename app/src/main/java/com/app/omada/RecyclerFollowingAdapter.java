package com.app.omada;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerFollowingAdapter extends RecyclerView.Adapter<RecyclerFollowingAdapter.ViewHolderFollowing> {
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mUserImageUrls = new ArrayList<>();
    Context mContext;
    public RecyclerFollowingAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mUserImageUrls){
        this.mNames = mNames;
        this.mUserImageUrls = mUserImageUrls;
        this.mContext = mContext;

    }
    @NonNull
    @Override
    public RecyclerFollowingAdapter.ViewHolderFollowing onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_list, parent,false);
        RecyclerFollowingAdapter.ViewHolderFollowing holder = new RecyclerFollowingAdapter.ViewHolderFollowing(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerFollowingAdapter.ViewHolderFollowing holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        if(!mUserImageUrls.get(position).equals("")){
            Glide.with(mContext)
                    .asBitmap()
                    .load(mUserImageUrls.get(position))
                    //.apply(requestOptions)
                    .override(150,50)
                    .fitCenter()
                    .placeholder(R.color.grayPlaceHolder)
                    .into(holder.userImageView);
        }
        holder.imageName.setText(mNames.get(position));
        holder.added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("Following").child(UsersInfo.getUsername()).child(mNames.get(position)).orderByChild("userFollowed").equalTo(mNames.get(position));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            mNames.remove(position);
                            mUserImageUrls.remove(position);
                            RecyclerFollowingAdapter.this.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                    }
                });
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                Query query2 = ref2.child("Following").child(mNames.get(position)).child(UsersInfo.getUsername()).orderByChild("userFollowed").equalTo(mNames.get(position));
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            RecyclerFollowingAdapter.this.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                        Log.e("Errrooorrrr", "onCancelled", databaseError.toException());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolderFollowing extends RecyclerView.ViewHolder{
        TextView imageName;
        ImageView userImageView;
        FloatingActionButton added,add;

        public ViewHolderFollowing(@NonNull View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.Sender);
            userImageView = itemView.findViewById(R.id.profile_image_recycler4);
            added = itemView.findViewById(R.id.floatingActionButton4);
        }
    }
}
