package com.matchpointplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.matchpointplus.adapters.MatchAdapter;
import com.matchpointplus.models.Match;
import com.matchpointplus.viewmodels.MatchesViewModel;
import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private static final String TAG = "MatchesActivity";
    private ViewPager2 viewPager;
    private MatchAdapter adapter;
    private List<Match> matches = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private MatchesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        viewModel = new ViewModelProvider(this).get(MatchesViewModel.class);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        viewPager = findViewById(R.id.viewPager);
        
        adapter = new MatchAdapter(matches, match -> showAiSummaryBottomSheet(match));
        viewPager.setAdapter(adapter);

        findViewById(R.id.menuButton).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                syncAndLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        FloatingActionButton passButton = findViewById(R.id.passButton);
        FloatingActionButton likeButton = findViewById(R.id.likeButton);

        passButton.setOnClickListener(v -> handleSwipe());
        likeButton.setOnClickListener(v -> handleSwipe());

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getMatches().observe(this, result -> {
            if (result != null && !result.isEmpty()) {
                matches.clear();
                matches.addAll(result);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Successfully fetched " + result.size() + " matches");
            } else if (result == null) {
                Log.e(TAG, "Error fetching matches");
                Toast.makeText(this, "Network error fetching profiles", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Fetched empty match list");
                Toast.makeText(this, "No matches found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncAndLogout() {
        viewModel.syncMatches(matches);
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (matches != null && !matches.isEmpty()) {
            viewModel.syncMatches(matches);
        }
    }

    private void handleSwipe() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < matches.size() - 1) {
            viewPager.setCurrentItem(currentItem + 1, true);
        } else {
            Toast.makeText(this, "No more matches near you", Toast.LENGTH_SHORT).show();
            findViewById(R.id.emptyStateTextView).setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    private void showAiSummaryBottomSheet(Match match) {
        AiSummaryBottomSheet bottomSheet = AiSummaryBottomSheet.newInstance(match);
        bottomSheet.show(getSupportFragmentManager(), "AiSummaryBottomSheet");
    }
}
