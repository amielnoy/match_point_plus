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
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.matchpointplus.models.User;
import com.matchpointplus.viewmodels.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profileImageView;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        setupToolbar();
        initViews();
        setupObservers();
        
        loadCurrentProfile();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        profileImageView = findViewById(R.id.profileImageView);
        findViewById(R.id.editProfileButton).setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void setupObservers() {
        viewModel.getUpdateStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                loadCurrentProfile();
            }
        });
    }

    private void loadCurrentProfile() {
        User currentUser = viewModel.getCurrentUser();
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    profileImageView.setImageBitmap(imageBitmap);
                    handleImageUpload(imageBitmap);
                }
            }
        }
    }

    private void handleImageUpload(Bitmap bitmap) {
        viewModel.uploadImage(bitmap).observe(this, publicUrl -> {
            if (publicUrl != null) {
                User user = viewModel.getCurrentUser();
                if (user != null) {
                    viewModel.updateProfilePicture(user.getId(), publicUrl);
                }
            } else {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
