package com.example.greenaura;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenaura.ChatApi;
import com.example.greenaura.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class
EcoChatBotActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private Retrofit retrofit;
    private ChatApi chatApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_chat_bot);

        inputMessage = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://172.20.10.3:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatApi = retrofit.create(ChatApi.class);

        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            if (!message.isEmpty()) {
                addMessageToChat(new Message(message, false));
                sendMessageToBot(message);
                inputMessage.setText("");
            }
        });
    }

    private void addMessageToChat(Message message) {
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessageToBot(String message) {
        // Add the EcoBot system prompt before the user's message
        String systemPrompt = "You are EcoBot, an eco-enthusiast chatbot dedicated to promoting sustainability, recycling, and environmental conservation. " +
                "You provide thoughtful, creative, and actionable advice on eco-friendly practices, recycling strategies, and ways to protect the Earth. " +
                "Your responses should be engaging, practical, and informative, catering to users who are passionate about creating a greener world. " +
                "Stay optimistic and inspiring, offering solutions that are both realistic and impactful, while encouraging users to adopt sustainable habits in their daily lives. " +
                "Keep your tone friendly and supportive, emphasizing the importance of collective action for a better planet.";

        String promptWithMessage = systemPrompt + "\n\nUser: " + message;

        chatApi.sendMessage(new ChatRequest(promptWithMessage)).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addMessageToChat(new Message(response.body().getReply(), true));
                } else {
                    addMessageToChat(new Message("Error: " + response.message(), true));
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                addMessageToChat(new Message("Failed to connect to server.", true));
            }
        });
    }

}

