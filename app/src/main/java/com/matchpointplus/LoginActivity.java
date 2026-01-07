package com.matchpointplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.matchpointplus.data.SupabaseManager;
import com.matchpointplus.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signupLink = findViewById(R.id.signupLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignUp();
            }
        });
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseManager.login(email, password, new SupabaseManager.SupabaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MatchesActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void performSignUp() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password to sign up", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseManager.signUp(email, password, new SupabaseManager.SupabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Account created! You can now log in.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
