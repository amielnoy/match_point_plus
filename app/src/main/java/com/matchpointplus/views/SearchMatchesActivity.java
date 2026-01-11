package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.matchpointplus.R;
import com.matchpointplus.adapters.SearchMatchAdapter;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.databinding.ActivitySearchMatchesBinding;
import com.matchpointplus.viewmodels.SearchViewModel;
import java.util.ArrayList;
import java.util.List;

public class SearchMatchesActivity extends AppCompatActivity {

    private ActivitySearchMatchesBinding binding;
    private SearchViewModel viewModel;
    private SearchMatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchMatchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        
        initViews();
        setupNavigation();
        setupAgeSlider();
        setupSearchInput();
        setupChipFilters();
        
        observeViewModel();
        
        viewModel.fetchAllCandidates();
    }

    private void initViews() {
        adapter = new SearchMatchAdapter(new ArrayList<>(), match -> {
            viewModel.addMatch(match, new SupabaseManager.SupabaseCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    runOnUiThread(() -> Toast.makeText(SearchMatchesActivity.this, match.getName() + " נוסף לרשימה שלך!", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(SearchMatchesActivity.this, "הוספה נכשלה", Toast.LENGTH_SHORT).show());
                }
            });
        });
        binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRecyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        binding.searchMenuButton.setOnClickListener(v -> binding.searchDrawerLayout.openDrawer(GravityCompat.START));

        binding.searchNavigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item.getItemId());
            binding.searchDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void handleNavigation(int id) {
        if (id == R.id.nav_matches) {
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_signup) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (id == R.id.nav_admin) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if (id == R.id.nav_logout) {
            navigateToLogin();
        }
    }

    private void setupAgeSlider() {
        binding.ageRangeSlider.setValueTo(55f);
        binding.ageRangeSlider.setValues(18f, 40f);
        binding.ageRangeSlider.setLabelFormatter(value -> String.valueOf((int) value));
        binding.ageRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> vals = slider.getValues();
            viewModel.updateAgeRange(Math.round(vals.get(0)), Math.round(vals.get(1)));
            binding.ageRangeDisplayTextView.setText(Math.round(vals.get(0)) + " - " + Math.round(vals.get(1)));
        });
    }

    private void setupSearchInput() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.updateSearchQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChipFilters() {
        for (int i = 0; i < binding.filterChipGroup.getChildCount(); i++) {
            View child = binding.filterChipGroup.getChildAt(i);
            if (child instanceof com.google.android.material.chip.Chip) {
                ((com.google.android.material.chip.Chip) child).setOnCheckedChangeListener((button, isChecked) -> 
                    viewModel.updateFilter(button.getText().toString(), isChecked));
            }
        }
    }

    private void observeViewModel() {
        viewModel.getFilteredMatches().observe(this, matches -> {
            if (matches != null) {
                adapter.setMatches(matches);
                binding.resultsCountTextView.setText("נמצאו " + matches.size() + " התאמות");
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
