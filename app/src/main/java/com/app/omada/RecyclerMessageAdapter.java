package com.app.omada;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMessageAdapter extends RecyclerView.Adapter<RecyclerMessageAdapter.ViewHolderMessage> {
    private Context mContext;
        String theLastMessage;
    private List<Chat> mChat;
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mDescr = new ArrayList<>();
    private ArrayList<String> mLastMessage = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList();
    public RecyclerMessageAdapter(Context mcontext, ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mLastMessage){
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mcontext;
        this.mLastMessage = mLastMessage;
    }
    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list, parent,false);

        ViewHolderMessage holder = new ViewHolderMessage(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderMessage holder, final int position) {
        try {
            holder.imageName.setText(mImageNames.get(position));
            if (mImages.size() != 0) {
                if (!mImages.get(position).equals("")) {
                    RequestOptions requestOptions = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(mImages.get(position))
                            .fitCenter()
                            .override(150, 50)
                            //.apply(requestOptions)
                            .placeholder(R.color.grayPlaceHolder)
                            .into(holder.imageView);
                }
            }
            holder.imageDescr.setText(mLastMessage.get(position));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), chatActivity.class);
                    myIntent.putExtra("username", mImageNames.get(position));
                    myIntent.putExtra("sender", UsersInfo.getUsername());
                    if(mImages.get(position) != null)
                         myIntent.putExtra("imageURL", mImages.get(position));

                    view.getContext().startActivity(myIntent);


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mImageNames != null)
             return mImageNames.size();
        else
            return 0;
    }
    public class ViewHolderMessage extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView imageName;
        TextView imageDescr;
        LinearLayout linearLayout;
        public ViewHolderMessage(@NonNull View itemView) {
            super(itemView);

            imageView  = itemView.findViewById(R.id.profile_image_recycler4);
            imageName = itemView.findViewById(R.id.Sender);
            imageDescr = itemView.findViewById(R.id.activity_status);
            linearLayout = itemView.findViewById(R.id.chatLayout);

        }
    }

}
