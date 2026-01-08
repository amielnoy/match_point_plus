package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.R;
import com.matchpointplus.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViews();
        
        loginButton.setOnClickListener(v -> performLogin());
        
        findViewById(R.id.signupLink).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        // וודא שיש ProgressBar ב-activity_login.xml, אם לא - נוסיף אותו עכשיו
        progressBar = findViewById(R.id.progressBar); 
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) return;

        setLoading(true);

        viewModel.login(email, password).observe(this, user -> {
            setLoading(false);
            if (user != null) {
                Toast.makeText(LoginActivity.this, "ברוכים הבאים ל-Sugar Meet!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MatchesActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "אימייל או סיסמה שגויים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("אנא הזן אימייל");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("אימייל לא תקין");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("אנא הזן סיסמה");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            return false;
        }
        return true;
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        loginButton.setEnabled(!isLoading);
        emailEditText.setEnabled(!isLoading);
        passwordEditText.setEnabled(!isLoading);
    }
}
