package com.app.omada;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerFollowerAdapter extends RecyclerView.Adapter<RecyclerFollowerAdapter.ViewHolderFollower> {
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mUserImageUrls = new ArrayList<>();
    Context mContext;
    public RecyclerFollowerAdapter(Context mContext, ArrayList<String> mNames, ArrayList<String> mUserImageUrls){
        this.mNames = mNames;
        this.mUserImageUrls = mUserImageUrls;
        this.mContext = mContext;

    }
    @NonNull
    @Override
    public RecyclerFollowerAdapter.ViewHolderFollower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_list, parent,false);
        RecyclerFollowerAdapter.ViewHolderFollower holder = new RecyclerFollowerAdapter.ViewHolderFollower(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerFollowerAdapter.ViewHolderFollower holder, final int position) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        if(!mUserImageUrls.get(position).equals("")){
            Glide.with(mContext)
                    .asBitmap()
                    .load(mUserImageUrls.get(position))
                    //.apply(requestOptions)
                    .fitCenter()
                    .override(150,50)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(holder.userImageView);
        }
        holder.imageName.setText(mNames.get(position));
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Following").child(UsersInfo.getUsername());
        db.addChildEventListener(new ChildEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("found this first "+snapshot.getValue().toString());


                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    System.out.println("found this second "+dataSnapshot.child("userFollowed").getValue().toString());

                    if (dataSnapshot.child("userFollowed").getValue().toString().equals(mNames.get(position))) {
                        holder.remove.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @SuppressLint("RestrictedApi")
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
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Following").child(UsersInfo.getUsername()).child(mNames.get(position)).push();
                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Following").child(mNames.get(position)).child(UsersInfo.getUsername()).push();

                String messagesender_ref = "Following/"+UsersInfo.getUsername() + "/" + mNames.get(position);
                String messagereceiver_ref = "Following/"+mNames.get(position) + "/" + UsersInfo.getUsername();
                String message_push_id = db.getKey();

                Map hash = new HashMap();
                hash.put("userFollowed",mNames.get(position));
                hash.put("userFollowing",UsersInfo.getUsername());
                Map hash2 = new HashMap();
                hash2.put("userFollowed",mNames.get(position));
                hash2.put("userFollowing",UsersInfo.getUsername());
                db2.updateChildren(hash2);

                db.updateChildren(hash).addOnSuccessListener(new OnSuccessListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(Object o) {
                        holder.add.setVisibility(View.INVISIBLE);
                        holder.remove.setVisibility(View.VISIBLE);
                        Toast.makeText(view.getContext(),"You are now following "+mNames.get(position), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("Following").child(UsersInfo.getUsername()).child(mNames.get(position)).orderByChild("userFollowed").equalTo(mNames.get(position));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            holder.add.setVisibility(View.VISIBLE);
                            holder.remove.setVisibility(View.INVISIBLE);
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
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            holder.add.setVisibility(View.VISIBLE);
                            holder.remove.setVisibility(View.INVISIBLE);
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

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolderFollower extends RecyclerView.ViewHolder{
        TextView imageName;
        ImageView userImageView;
        FloatingActionButton remove,add;

        public ViewHolderFollower(@NonNull View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.Sender);
            userImageView = itemView.findViewById(R.id.profile_image_recycler4);
            remove = itemView.findViewById(R.id.floatingActionButton4);
            add = itemView.findViewById(R.id.add_button);

        }
    }
}
