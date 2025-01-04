package com.example.greenaura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter for handling chat messages
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // List of messages to display
    private final List<Message> messages;

    // Constructor for the adapter
    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on the message type
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == 0 ? R.layout.user_message_item : R.layout.bot_message_item,
                parent,
                false
        );
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Bind data to the view holder
        Message message = messages.get(position);
        holder.messageText.setText(message.getText());

        // Set the visibility of bot or user icon if required
        if (holder.botIcon != null) {
            holder.botIcon.setVisibility(message.isBot() ? View.VISIBLE : View.GONE);
        }
        if (holder.userIcon != null) {
            holder.userIcon.setVisibility(message.isBot() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Return 0 for user messages, 1 for bot messages
        return messages.get(position).isBot() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder class to hold and manage message views
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView botIcon, userIcon;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            // Optional: Check for bot/user icons
            botIcon = itemView.findViewById(R.id.bot_icon); // Only in bot layout
            userIcon = itemView.findViewById(R.id.user_icon); // Only in user layout
        }
    }
}
