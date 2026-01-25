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
    private final List<Message> messages = new ArrayList<>();
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

        adapter = new MessageAdapter(messages);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(adapter);

        binding.sendButton.setOnClickListener(v -> sendMessage());
        
        observeViewModel();
        
        // טעינת היסטוריה והפעלת Realtime
        viewModel.startRealtimeUpdates(receiverId);
    }

    private void observeViewModel() {
        viewModel.getMessages(receiverId).observe(this, result -> {
            if (result != null) {
                // עדכון הרשימה רק אם היא השתנתה באמת
                if (result.size() != messages.size()) {
                    messages.clear();
                    messages.addAll(result);
                    adapter.notifyDataSetChanged();
                    binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });
    }

    private void sendMessage() {
        String text = binding.messageEditText.getText().toString().trim();
        if (!text.isEmpty()) {
            // 1. יצירת הודעה חדשה
            Message newMessage = new Message(text, true, receiverId);
            
            // 2. הוספה מיידית ל-UI (חוויה מהירה למשתמש)
            messages.add(newMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
            binding.messageEditText.setText("");

            // 3. שמירה בענן
            viewModel.sendMessage(newMessage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopRealtimeUpdates();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
