package com.example.luxevistaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            redirectToAppropriateScreen(isAdmin);
        }

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupTextWatchers();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupTextWatchers() {
        // Email text watcher
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password text watcher
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    loginUser();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 4) {
            passwordInputLayout.setError("Password must be at least 4 characters");
            isValid = false;
        }

        return isValid;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Check for admin login
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            handleAdminLogin();
            return;
        }

        // Regular user login
        Cursor cursor = dbHelper.getUser(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            handleUserLogin(cursor);
        } else {
            showLoginError();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void handleAdminLogin() {
        // Save login state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isAdmin", true);
        editor.apply();

        Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    private void handleUserLogin(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

        // Save login state and user info
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isAdmin", false);
        editor.putInt("userId", userId);
        editor.putString("username", username);
        editor.apply();

        Toast.makeText(this, "Login successful! Welcome back, " + username, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, GuestDashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    private void showLoginError() {
        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        // Shake animation for error feedback
        emailInputLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
        passwordInputLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void redirectToAppropriateScreen(boolean isAdmin) {
        if (isAdmin) {
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, GuestDashboardActivity.class);
            startActivity(intent);
        }
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
}