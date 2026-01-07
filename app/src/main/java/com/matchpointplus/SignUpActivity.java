package com.matchpointplus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.matchpointplus.viewmodels.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        emailEditText = findViewById(R.id.signupEmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        Button signupButton = findViewById(R.id.signupButton);
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

        viewModel.signUp(email, password).observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(SignUpActivity.this, "החשבון נוצר בהצלחה! ניתן להתחבר.", Toast.LENGTH_LONG).show();
                finish(); // חזרה למסך התחברות
            } else {
                Toast.makeText(SignUpActivity.this, "ההרשמה נכשלה. ייתכן והאימייל כבר קיים.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
