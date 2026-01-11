package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.RangeSlider;
import com.matchpointplus.R;
import com.matchpointplus.adapters.SearchMatchAdapter;
import com.matchpointplus.viewmodels.SearchViewModel;
import com.matchpointplus.data.SupabaseManager;
import java.util.ArrayList;
import java.util.List;

public class SearchMatchesActivity extends AppCompatActivity {

    private SearchViewModel viewModel;
    private SearchMatchAdapter adapter;
    private TextView resultsCountTextView;
    private TextView ageRangeDisplayTextView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_matches);

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
        drawerLayout = findViewById(R.id.searchDrawerLayout);
        resultsCountTextView = findViewById(R.id.resultsCountTextView);
        ageRangeDisplayTextView = findViewById(R.id.ageRangeDisplayTextView);
        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        ImageButton menuButton = findViewById(R.id.searchMenuButton);
        NavigationView navigationView = findViewById(R.id.searchNavigationView);

        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                if (drawerLayout != null) drawerLayout.openDrawer(GravityCompat.START);
            });
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                handleNavigation(item.getItemId());
                if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
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
        RangeSlider ageSlider = findViewById(R.id.ageRangeSlider);
        if (ageSlider != null) {
            ageSlider.setValueTo(55f);
            ageSlider.setValues(18f, 40f);
            ageSlider.setLabelFormatter(value -> String.valueOf((int) value));
            ageSlider.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> vals = slider.getValues();
                viewModel.updateAgeRange(Math.round(vals.get(0)), Math.round(vals.get(1)));
                if (ageRangeDisplayTextView != null) {
                    ageRangeDisplayTextView.setText(Math.round(vals.get(0)) + " - " + Math.round(vals.get(1)));
                }
            });
        }
    }

    private void setupSearchInput() {
        EditText searchEditText = findViewById(R.id.searchEditText);
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewModel.updateSearchQuery(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupChipFilters() {
        ChipGroup filterChipGroup = findViewById(R.id.filterChipGroup);
        if (filterChipGroup != null) {
            for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
                View child = filterChipGroup.getChildAt(i);
                if (child instanceof Chip) {
                    ((Chip) child).setOnCheckedChangeListener((button, isChecked) -> 
                        viewModel.updateFilter(button.getText().toString(), isChecked));
                }
            }
        }
    }

    private void observeViewModel() {
        viewModel.getFilteredMatches().observe(this, matches -> {
            if (matches != null) {
                adapter.setMatches(matches);
                resultsCountTextView.setText("נמצאו " + matches.size() + " התאמות");
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
