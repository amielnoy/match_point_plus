package com.matchpointplus.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.matchpointplus.R;
import com.matchpointplus.data.MockData;
import com.matchpointplus.data.SupabaseManager;
import java.util.Collections;

public class AdminActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        progressBar = findViewById(R.id.adminProgressBar);
        statusTextView = findViewById(R.id.statusTextView);

        findViewById(R.id.seedDataButton).setOnClickListener(v -> performSeed());
        findViewById(R.id.clearDataButton).setOnClickListener(v -> performReset());
    }

    private void performSeed() {
        setLoading(true, "מעלה נתוני Mock לענן...");
        SupabaseManager.saveMatches(MockData.getUsers(), new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    setLoading(false, "הנתונים נטענו בהצלחה!");
                    Toast.makeText(AdminActivity.this, "סינכרון הושלם", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> setLoading(false, "שגיאה בטעינה: " + e.getMessage()));
            }
        });
    }

    private void performReset() {
        setLoading(true, "מנקה נתונים מהענן...");
        // ב-Supabase מחיקה מתבצעת על ידי שליחת רשימה ריקה או קריאת DELETE ייעודית
        // כרגע נשתמש בעדכון סטטוסים ל-false כאיפוס זמני, או נממש DELETE בעתיד
        Toast.makeText(this, "פונקציית איפוס מלא תמומש בגרסה הבאה", Toast.LENGTH_SHORT).show();
        setLoading(false, "מוכן");
    }

    private void setLoading(boolean isLoading, String status) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        statusTextView.setText("סטטוס: " + status);
    }
}
