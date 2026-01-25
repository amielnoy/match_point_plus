package com.matchpointplus.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.databinding.ActivityLoginBinding;
import com.matchpointplus.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> performLogin());
        
        binding.signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) return;

        setLoading(true);
        Log.d(TAG, "Attempting login for: " + email);

        viewModel.login(email, password).observe(this, user -> {
            setLoading(false);
            if (user != null) {
                Log.d(TAG, "Login successful: " + user.getEmail());
                Toast.makeText(LoginActivity.this, "ברוכים הבאים ל-שוגר מיט!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MatchesActivity.class));
                finish();
            } else {
                Log.e(TAG, "Login failed: No user found or network error");
                Toast.makeText(LoginActivity.this, "אימייל או סיסמה שגויים (או בעיית הרשאות בשרת)", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            binding.emailEditText.setError("אנא הזן אימייל");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("אימייל לא תקין");
            return false;
        }
        if (password.isEmpty()) {
            binding.passwordEditText.setError("אנא הזן סיסמה");
            return false;
        }
        return true;
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.loginButton.setEnabled(!isLoading);
        binding.emailEditText.setEnabled(!isLoading);
        binding.passwordEditText.setEnabled(!isLoading);
    }
}
