package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.databinding.ActivityAdminBinding;
import com.matchpointplus.viewmodels.AdminViewModel;

public class AdminActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.addCandidateButton.setOnClickListener(v -> 
            startActivity(new Intent(this, AddCandidateActivity.class)));

        binding.seedDataButton.setOnClickListener(v -> viewModel.seedMockData());
        
        binding.clearDataButton.setOnClickListener(v -> viewModel.resetData());
    }

    private void setupObservers() {
        viewModel.getStatusMessage().observe(this, status -> {
            if (status != null) binding.statusTextView.setText("סטטוס: " + status);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.adminProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}
