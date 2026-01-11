package com.matchpointplus.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.matchpointplus.databinding.ActivityChatBinding;
import com.matchpointplus.adapters.MessageAdapter;
import com.matchpointplus.models.Message;
import com.matchpointplus.viewmodels.ChatViewModel;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private MessageAdapter adapter;
    private List<Message> messages;
    private String receiverId;
    private ChatViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        String userName = getIntent().getStringExtra("user_name");
        receiverId = getIntent().getStringExtra("user_id");

        setSupportActionBar(binding.chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(adapter);

        binding.sendButton.setOnClickListener(v -> sendMessage());
        
        observeViewModel();
        
        // Start real-time listening
        viewModel.startRealtimeUpdates(receiverId);
    }

    private void observeViewModel() {
        viewModel.getMessages(receiverId).observe(this, result -> {
            if (result != null) {
                messages.clear();
                messages.addAll(result);
                adapter.notifyDataSetChanged();
                binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage() {
        String text = binding.messageEditText.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(text, true, receiverId);
            // Optimized: We don't manually add to local list here, 
            // the realtime callback will handle the update.
            viewModel.sendMessage(newMessage);
            binding.messageEditText.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        // Stop real-time listening to save battery/bandwidth
        viewModel.stopRealtimeUpdates();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
