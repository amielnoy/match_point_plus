package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.matchpointplus.R;
import com.matchpointplus.adapters.MessagesListAdapter;
import com.matchpointplus.models.Match;
import com.matchpointplus.viewmodels.MessagesViewModel;
import java.util.ArrayList;
import java.util.List;

public class MessagesListActivity extends AppCompatActivity {

    private MessagesViewModel viewModel;
    private MessagesListAdapter adapter;
    private final List<Match> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        viewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        initViews();
        setupObservers();
        
        viewModel.fetchContacts();
    }

    private void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        adapter = new MessagesListAdapter(contacts, this::openChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getContacts().observe(this, result -> {
            if (result != null) {
                contacts.clear();
                contacts.addAll(result);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChat(Match contact) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_name", contact.getName());
        intent.putExtra("user_id", contact.getId());
        startActivity(intent);
    }
}
