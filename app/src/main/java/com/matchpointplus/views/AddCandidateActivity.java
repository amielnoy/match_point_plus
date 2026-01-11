package com.matchpointplus.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import java.io.IOException;
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
        
        findViewById(R.id.uploadImageButton).setOnClickListener(v -> pickImageFromGallery());
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                uploadToCloud(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "טעינת התמונה נכשלה", Toast.LENGTH_SHORT).show();
            }
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
                    Toast.makeText(AddCandidateActivity.this, "התמונה הועלתה בהצלחה", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "העלאה נכשלה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "אנא מלא שדות חובה", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        
        // יצירת מועמד חדש - שים לב ל-is_selected = false כברירת מחדל
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
        newMatch.setSelected(false); 

        SupabaseManager.saveMatches(Collections.singletonList(newMatch), new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "המועמד נשמר בהצלחה!", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(AddCandidateActivity.this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
