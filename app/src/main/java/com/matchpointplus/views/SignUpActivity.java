package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.R;
import com.matchpointplus.viewmodels.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText emailEditText;
    private EditText passwordEditText;
    private SignUpViewModel viewModel;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        emailEditText = findViewById(R.id.signupEmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        TextView loginLink = findViewById(R.id.loginLink);

        signupButton.setOnClickListener(v -> performSignUp());
        loginLink.setOnClickListener(v -> finish());
    }

    private void performSignUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show();
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("רושם אותך...");

        Log.d(TAG, "מתחיל תהליך הרשמה עבור: " + email);

        viewModel.signUp(email, password).observe(this, success -> {
            signupButton.setEnabled(true);
            signupButton.setText("הירשם עכשיו");

            if (Boolean.TRUE.equals(success)) {
                Log.d(TAG, "הרשמה הצליחה!");
                Toast.makeText(SignUpActivity.this, "החשבון נוצר! בוא נשלים את הפרופיל שלך", Toast.LENGTH_LONG).show();
                
                // מעבר למסך הוספת פרטים נוספים
                Intent intent = new Intent(SignUpActivity.this, AddCandidateActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "הרשמה נכשלה בשרת");
                Toast.makeText(SignUpActivity.this, "ההרשמה נכשלה. בדוק את החיבור או נסה אימייל אחר.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
