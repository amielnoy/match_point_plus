package com.matchpointplus.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.matchpointplus.R;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.databinding.ActivityProfileBinding;
import com.matchpointplus.models.User;
import com.matchpointplus.viewmodels.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 101;
    
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        setupToolbar();
        setupListeners();
        setupObservers();
        
        displayUserData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.profileToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.my_profile);
        }
    }

    private void setupListeners() {
        binding.editProfileButton.setOnClickListener(v -> checkCameraPermissionAndLaunch());
    }

    private void setupObservers() {
        viewModel.getUpdateStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                displayUserData();
            }
        });
    }

    private void displayUserData() {
        User currentUser = viewModel.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String firstName = (email != null && email.contains("@")) ? email.split("@")[0] : "משתמש";
            binding.userNameTextView.setText(getString(R.string.hello_user, firstName));
            binding.userEmailTextView.setText(email);
            
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getProfilePicture())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .circleCrop()
                        .into(binding.profileImageView);
            }
        }
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Log.e(TAG, "Camera failed to open: " + e.getMessage());
            Toast.makeText(this, "לא ניתן לפתוח את המצלמה", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "דרושה הרשאת מצלמה כדי לעדכן תמונה", Toast.LENGTH_SHORT).show();
            }
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
                    binding.profileImageView.setImageBitmap(imageBitmap);
                    handleImageUpload(imageBitmap);
                }
            }
        }
    }

    private void handleImageUpload(Bitmap bitmap) {
        Toast.makeText(this, "מעלה תמונה...", Toast.LENGTH_SHORT).show();
        // Updated to use a safer callback structure to get the error message
        SupabaseManager.uploadImage(bitmap, new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String publicUrl) {
                runOnUiThread(() -> {
                    User user = viewModel.getCurrentUser();
                    if (user != null) {
                        viewModel.updateProfilePicture(user.getId(), publicUrl);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Upload failed: " + e.getMessage());
                    Toast.makeText(ProfileActivity.this, "שגיאת שרת: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
