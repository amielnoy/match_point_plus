package com.matchpointplus;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.matchpointplus.adapters.MessageAdapter;
import com.matchpointplus.models.Message;
import com.matchpointplus.viewmodels.ChatViewModel;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private MessageAdapter adapter;
    private List<Message> messages;
    private EditText messageEditText;
    private String receiverId;
    private ChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

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
        
        adapter = new MessageAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        findViewById(R.id.sendButton).setOnClickListener(v -> sendMessage());
        
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getMessages(receiverId).observe(this, result -> {
            if (result != null && !result.isEmpty()) {
                messages.clear();
                messages.addAll(result);
                adapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
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

            viewModel.sendMessage(newMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messages != null && !messages.isEmpty()) {
            viewModel.syncMessages(messages);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
