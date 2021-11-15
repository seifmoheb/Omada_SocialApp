package com.app.omada;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private ArrayList<Messages> userMessagesList;
    private DatabaseReference databaseReference;
    public MessagesAdapter(ArrayList<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users,parent,false);
        return new MessagesViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesAdapter.MessagesViewHolder holder, final int position) {


        try {
            final String messageSenderId = UsersInfo.getUsername();
            final Messages messages = userMessagesList.get(position);
            String type = messages.getType();
            String fromUserId = messages.getSender();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(fromUserId);
            holder.senderMessageText.setVisibility(View.INVISIBLE);
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.senderMessagePdf.setVisibility(View.INVISIBLE);
            holder.receiverMessagePdf.setVisibility(View.INVISIBLE);
            if (type.equals("text")) {
                if (fromUserId.equals(messageSenderId)) {
                    holder.senderMessageText.setVisibility(View.VISIBLE);
                    holder.senderMessageText.setBackgroundResource(R.drawable.background_right);
                    holder.senderMessageText.setTextColor(Color.WHITE);
                    holder.senderMessageText.setGravity(Gravity.LEFT);
                    holder.senderMessageText.setText(messages.getMessage());
                    holder.senderMessageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view.getRootView(), messages.getTime() + ", " + messages.getDate(), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.receiverMessageText.setBackgroundResource(R.drawable.background_left);
                    holder.receiverMessageText.setTextColor(Color.WHITE);
                    holder.receiverMessageText.setGravity(Gravity.LEFT);
                    holder.receiverMessageText.setText(messages.getMessage());
                    holder.receiverMessageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view.getRootView(), messages.getTime() + ", " + messages.getDate(), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            } else {
                if (fromUserId.equals(messageSenderId)) {
                    holder.senderMessagePdf.setVisibility(View.VISIBLE);

                    holder.senderMessagePdf.setBackgroundResource(R.drawable.file);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });
                } else {
                    holder.receiverMessagePdf.setVisibility(View.VISIBLE);

                    holder.receiverMessagePdf.setBackgroundResource(R.drawable.file);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public class MessagesViewHolder extends  RecyclerView.ViewHolder{
        public TextView senderMessageText;
        public TextView receiverMessageText;
        public ImageView senderMessagePdf;
        public ImageView receiverMessagePdf;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            senderMessagePdf = itemView.findViewById(R.id.message_sender_image_view);
            receiverMessagePdf = itemView.findViewById(R.id.message_receiver_image_view);
        }
    }

}
