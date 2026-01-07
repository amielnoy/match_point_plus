package com.matchpointplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.User;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileImageView = findViewById(R.id.profileImageView);
        loadCurrentProfile();

        findViewById(R.id.editProfileButton).setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void loadCurrentProfile() {
        User currentUser = SupabaseManager.getCurrentUser();
        if (currentUser != null && currentUser.getProfilePicture() != null) {
            Glide.with(this)
                    .load(currentUser.getProfilePicture())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(profileImageView);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            
            // Show preview immediately
            profileImageView.setImageBitmap(imageBitmap);

            // Upload to Supabase Storage
            uploadAndSaveImage(imageBitmap);
        }
    }

    private void uploadAndSaveImage(Bitmap bitmap) {
        SupabaseManager.uploadImage(bitmap, new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                saveProfilePictureToDb(publicUrl);
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveProfilePictureToDb(String imageUrl) {
        User user = SupabaseManager.getCurrentUser();
        if (user != null) {
            SupabaseManager.updateProfilePicture(user.getId(), imageUrl, new SupabaseManager.SupabaseCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                        // Refresh image from DB URL to ensure sync
                        loadCurrentProfile();
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Database update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
