package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.matchpointplus.R;
import com.matchpointplus.adapters.MessagesListAdapter;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.ArrayList;
import java.util.List;

public class MessagesListActivity extends AppCompatActivity {

    private MessagesListAdapter adapter;
    private final List<Match> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        setupToolbar();
        setupRecyclerView();
        fetchContacts();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Using custom toolbar from XML
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        adapter = new MessagesListAdapter(contacts, this::openChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void fetchContacts() {
        SupabaseManager.fetchMatches(new SupabaseManager.SupabaseCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                runOnUiThread(() -> {
                    if (result != null) {
                        contacts.clear();
                        contacts.addAll(result);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(MessagesListActivity.this, "שגיאה בטעינת אנשי קשר", Toast.LENGTH_SHORT).show());
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
