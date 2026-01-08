package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.matchpointplus.R;
import com.matchpointplus.adapters.MatchAdapter;
import com.matchpointplus.models.Match;
import com.matchpointplus.viewmodels.MatchesViewModel;
import com.matchpointplus.AiSummaryBottomSheet;
import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private MatchesViewModel viewModel;
    private MatchAdapter adapter;
    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private TextView emptyStateTextView;
    private final List<Match> matches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        viewModel = new ViewModelProvider(this).get(MatchesViewModel.class);

        initViews();
        setupNavigation();
        setupActionButtons();
        setupObservers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshMatches();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        
        adapter = new MatchAdapter(matches, this::showAiSummaryBottomSheet);
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
        }
    }

    private void setupNavigation() {
        findViewById(R.id.menuButton).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                handleNavigation(item.getItemId());
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    private void handleNavigation(int itemId) {
        if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_search) {
            startActivity(new Intent(this, SearchMatchesActivity.class));
        } else if (itemId == R.id.nav_admin) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if (itemId == R.id.nav_logout) {
            performLogout();
        }
    }

    private void setupActionButtons() {
        findViewById(R.id.passButton).setOnClickListener(v -> handleSwipe());
        findViewById(R.id.likeButton).setOnClickListener(v -> handleSwipe());
    }

    private void setupObservers() {
        viewModel.getMatches().observe(this, result -> {
            matches.clear();
            if (result != null && !result.isEmpty()) {
                matches.addAll(result);
                adapter.notifyDataSetChanged();
                toggleEmptyState(false);
            } else {
                toggleEmptyState(true);
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (emptyStateTextView != null) emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (viewPager != null) viewPager.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void handleSwipe() {
        if (viewPager == null || matches.isEmpty()) return;
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < matches.size() - 1) {
            viewPager.setCurrentItem(currentItem + 1, true);
        } else {
            toggleEmptyState(true);
        }
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAiSummaryBottomSheet(Match match) {
        AiSummaryBottomSheet.newInstance(match).show(getSupportFragmentManager(), "AiSummaryBottomSheet");
    }
}
