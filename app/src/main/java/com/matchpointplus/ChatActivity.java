package com.matchpointplus;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.matchpointplus.adapters.MessageAdapter;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private MessageAdapter adapter;
    private List<Message> messages;
    private EditText messageEditText;
    private String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String userName = getIntent().getStringExtra("user_name");
        receiverId = getIntent().getStringExtra("user_id");

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        messages = new ArrayList<>();
        
        // Use receiverId in the constructor
        messages.add(new Message("Hi! I saw we have common interests. How are you?", false, receiverId));

        adapter = new MessageAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        findViewById(R.id.sendButton).setOnClickListener(v -> sendMessage());
        
        loadChatHistory();
    }

    private void loadChatHistory() {
        SupabaseManager.fetchMessages(receiverId, new SupabaseManager.SupabaseCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                runOnUiThread(() -> {
                    if (result != null && !result.isEmpty()) {
                        messages.clear();
                        messages.addAll(result);
                        adapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(text, true, receiverId);
            messages.add(newMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);
            messageEditText.setText("");

            // Save to Supabase with error handling
            SupabaseManager.saveMessage(newMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Sync all messages when leaving the chat
        if (messages != null && !messages.isEmpty()) {
            SupabaseManager.saveMessages(messages, null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
