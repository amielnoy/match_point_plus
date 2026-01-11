package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.R;
import com.matchpointplus.viewmodels.AdminViewModel;

public class AdminActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private ProgressBar progressBar;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        initViews();
        setupListeners();
        setupObservers();
    }

    private void initViews() {
        progressBar = findViewById(R.id.adminProgressBar);
        statusTextView = findViewById(R.id.statusTextView);
    }

    private void setupListeners() {
        findViewById(R.id.addCandidateButton).setOnClickListener(v -> 
            startActivity(new Intent(this, AddCandidateActivity.class)));

        findViewById(R.id.seedDataButton).setOnClickListener(v -> viewModel.seedMockData());
        
        findViewById(R.id.clearDataButton).setOnClickListener(v -> viewModel.resetData());
    }

    private void setupObservers() {
        viewModel.getStatusMessage().observe(this, status -> {
            if (status != null) statusTextView.setText("סטטוס: " + status);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}
