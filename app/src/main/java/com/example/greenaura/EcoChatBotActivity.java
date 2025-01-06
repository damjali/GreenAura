package com.example.greenaura;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class
EcoChatBotActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private ImageView imageview;

    private Retrofit retrofit;
    private ChatApi chatApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_chat_bot);

        inputMessage = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        imageview = findViewById(R.id.groupphoto);
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
                handleCustomResponse(message);
                inputMessage.setText("");
            }
        });

        // Set up the input type to handle URL suggestions and display keyboard hints for links
        inputMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
        inputMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        inputMessage.setHint("Type a message or paste a link...");
    }

    private void addMessageToChat(Message message) {
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void handleCustomResponse(String userMessage) {
        if (userMessage.equalsIgnoreCase("Who created this app?")) {
            String customResponse = "GreenAura was created by five young and intelligent individuals committed to making the world a greener place. With passion and teamwork, they aim to drive sustainable change.\n\n" +
                    "A special thanks to Dr. Hazim, our beloved Mobile Application Development Lecturer, for inspiring us. We hope he awards us full marks for our effort!\n\n" +
                    "Here's a photo of the team striving for a better future";

            addMessageToChat(new Message(customResponse, true));

            // Show the ImageView
            imageview.setVisibility(View.VISIBLE);
        } else {
            sendMessageToBot(userMessage);
        }
    }


    private void sendMessageToBot(String message) {
        String systemPrompt = "You are EcoBot, an eco-enthusiast chatbot dedicated to promoting sustainability, recycling, and environmental conservation. " +
                "You provide thoughtful, creative, and actionable advice on eco-friendly practices, recycling strategies, and ways to protect the Earth. " +
                "Your responses should be engaging, practical, and informative, catering to users who are passionate about creating a greener world. " +
                "Stay optimistic and inspiring, offering solutions that are both realistic and impactful, while encouraging users to adopt sustainable habits in their daily lives. " +
                "Keep your tone friendly and supportive, emphasizing the importance of collective action for a better planet. Please don’t elaborate too much; make it simple and concise.";

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
