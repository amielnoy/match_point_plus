package com.matchpointplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.matchpointplus.adapters.UserAdapter;
import com.matchpointplus.data.MockData;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.User;
import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private UserAdapter adapter;
    private List<User> users = new ArrayList<>();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        viewPager = findViewById(R.id.viewPager);
        
        adapter = new UserAdapter(users, user -> showAiSummaryBottomSheet(user));
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

        seedAndFetchUsers();
    }

    private void seedAndFetchUsers() {
        // Seed mock data using the bulk save method
        SupabaseManager.saveUsers(MockData.getUsers(), null);

        // Fetch actual data from Supabase
        SupabaseManager.fetchUsers(new SupabaseManager.SupabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                runOnUiThread(() -> {
                    if (result != null && !result.isEmpty()) {
                        users.clear();
                        users.addAll(result);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(MatchesActivity.this, "Error fetching profiles", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void syncAndLogout() {
        // Sync before logging out
        SupabaseManager.saveUsers(users, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                navigateToLogin();
            }

            @Override
            public void onError(Exception e) {
                navigateToLogin(); // Still logout even if sync fails
            }
        });
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
        // Automatically sync data when the app is backgrounded
        if (users != null && !users.isEmpty()) {
            SupabaseManager.saveUsers(users, null);
        }
    }

    private void handleSwipe() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < users.size() - 1) {
            viewPager.setCurrentItem(currentItem + 1, true);
        } else {
            Toast.makeText(this, "No more matches near you", Toast.LENGTH_SHORT).show();
            findViewById(R.id.emptyStateTextView).setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    private void showAiSummaryBottomSheet(User user) {
        AiSummaryBottomSheet bottomSheet = AiSummaryBottomSheet.newInstance(user);
        bottomSheet.show(getSupportFragmentManager(), "AiSummaryBottomSheet");
    }
}
