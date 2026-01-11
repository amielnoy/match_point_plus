package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.R;
import com.matchpointplus.adapters.MatchAdapter;
import com.matchpointplus.databinding.ActivityMatchesBinding;
import com.matchpointplus.models.Match;
import com.matchpointplus.viewmodels.MatchesViewModel;
import com.matchpointplus.AiSummaryBottomSheet;
import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private ActivityMatchesBinding binding;
    private MatchesViewModel viewModel;
    private MatchAdapter adapter;
    private final List<Match> matches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMatchesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        adapter = new MatchAdapter(matches, this::showAiSummaryBottomSheet);
        binding.viewPager.setAdapter(adapter);
    }

    private void setupNavigation() {
        binding.menuButton.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
        
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigation(item.getItemId());
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void handleNavigation(int itemId) {
        if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_search) {
            startActivity(new Intent(this, SearchMatchesActivity.class));
        } else if (itemId == R.id.nav_messages) {
            startActivity(new Intent(this, MessagesListActivity.class));
        } else if (itemId == R.id.nav_signup) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (itemId == R.id.nav_admin) {
            startActivity(new Intent(this, AdminActivity.class));
        } else if (itemId == R.id.nav_logout) {
            performLogout();
        }
    }

    private void setupActionButtons() {
        binding.passButton.setOnClickListener(v -> handlePass());
    }

    private void setupObservers() {
        viewModel.getMatches().observe(this, result -> {
            matches.clear();
            if (result != null && !result.isEmpty()) {
                matches.addAll(result);
                adapter.notifyDataSetChanged();
                toggleEmptyState(false);
            } else {
                adapter.notifyDataSetChanged();
                toggleEmptyState(true);
            }
        });
    }

    private void toggleEmptyState(boolean isEmpty) {
        binding.emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.viewPager.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void handlePass() {
        if (matches.isEmpty()) return;
        
        int currentItem = binding.viewPager.getCurrentItem();
        if (currentItem >= 0 && currentItem < matches.size()) {
            Match matchToRemove = matches.get(currentItem);
            viewModel.removeMatch(matchToRemove);
            Toast.makeText(this, matchToRemove.getName() + " הוסר מהרשימה", Toast.LENGTH_SHORT).show();
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
