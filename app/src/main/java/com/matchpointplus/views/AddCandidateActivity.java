package com.matchpointplus.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.matchpointplus.R;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.Match;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class AddCandidateActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView imageView;
    private EditText nameEt, ageEt, locationEt, bioEt, interestsEt;
    private ProgressBar progressBar;
    private String uploadedImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        initViews();
        
        findViewById(R.id.uploadImageButton).setOnClickListener(v -> pickImage());
        findViewById(R.id.saveCandidateButton).setOnClickListener(v -> saveCandidate());
    }

    private void initViews() {
        imageView = findViewById(R.id.candidateImageView);
        nameEt = findViewById(R.id.nameEditText);
        ageEt = findViewById(R.id.ageEditText);
        locationEt = findViewById(R.id.locationEditText);
        bioEt = findViewById(R.id.bioEditText);
        interestsEt = findViewById(R.id.interestsEditText);
        progressBar = findViewById(R.id.saveProgressBar);
    }

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            uploadToCloud(bitmap);
        }
    }

    private void uploadToCloud(Bitmap bitmap) {
        setLoading(true);
        SupabaseManager.uploadImage(bitmap, new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                uploadedImageUrl = result;
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveCandidate() {
        String name = nameEt.getText().toString().trim();
        String ageStr = ageEt.getText().toString().trim();
        String location = locationEt.getText().toString().trim();
        String bio = bioEt.getText().toString().trim();
        String interestsStr = interestsEt.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        Match newMatch = new Match(
                UUID.randomUUID().toString(),
                name,
                Integer.parseInt(ageStr),
                location,
                bio,
                Arrays.asList(interestsStr.split(",")),
                uploadedImageUrl,
                Collections.singletonList(uploadedImageUrl)
        );

        SupabaseManager.saveMatches(Collections.singletonList(newMatch), new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "Candidate Saved!", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
