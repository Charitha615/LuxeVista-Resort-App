package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etContact, etAddress, etPassword, etConfirmPassword;
    private TextInputLayout usernameInputLayout, emailInputLayout, contactInputLayout, addressInputLayout,
            passwordInputLayout, confirmPasswordInputLayout, countryInputLayout;
    private RadioGroup rgGender;
    private AutoCompleteTextView spCountry;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private DatabaseHelper dbHelper;

    private String selectedGender = "", selectedCountry = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupCountrySpinner();
        setupTextWatchers();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        contactInputLayout = findViewById(R.id.contactInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        countryInputLayout = findViewById(R.id.countryInputLayout);

        rgGender = findViewById(R.id.rgGender);
        spCountry = findViewById(R.id.spCountry);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbMale) {
                    selectedGender = "Male";
                } else if (checkedId == R.id.rbFemale) {
                    selectedGender = "Female";
                }
            }
        });
    }

    private void setupCountrySpinner() {
        // Get countries array from resources
        String[] countries = getResources().getStringArray(R.array.countries_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_menu_popup_item, countries);
        spCountry.setAdapter(adapter);

        // Set item selection listener
        spCountry.setOnItemClickListener((parent, view, position, id) -> {
            selectedCountry = parent.getItemAtPosition(position).toString();
            countryInputLayout.setError(null);
        });
    }

    private void setupTextWatchers() {
        // Setup text watchers for all input fields
        setupTextWatcher(etUsername, usernameInputLayout);
        setupTextWatcher(etEmail, emailInputLayout);
        setupTextWatcher(etContact, contactInputLayout);
        setupTextWatcher(etAddress, addressInputLayout);
        setupTextWatcher(etPassword, passwordInputLayout);
        setupTextWatcher(etConfirmPassword, confirmPasswordInputLayout);

        // Special text watcher for password confirmation
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordInputLayout.setError(null);
                validatePasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTextWatcher(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    registerUser();
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private boolean validateInputs() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            usernameInputLayout.setError("Username must be at least 3 characters");
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate contact
        if (TextUtils.isEmpty(contact)) {
            contactInputLayout.setError("Contact number is required");
            isValid = false;
        } else if (contact.length() < 10) {
            contactInputLayout.setError("Please enter a valid contact number");
            isValid = false;
        }

        // Validate address
        if (TextUtils.isEmpty(address)) {
            addressInputLayout.setError("Address is required");
            isValid = false;
        } else if (address.length() < 10) {
            addressInputLayout.setError("Please enter a complete address");
            isValid = false;
        }

        // Validate gender
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Validate country
        if (selectedCountry.isEmpty() || selectedCountry.equals("Select Country")) {
            countryInputLayout.setError("Please select your country");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void validatePasswordMatch() {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) &&
                !password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
        } else {
            confirmPasswordInputLayout.setError(null);
        }
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (dbHelper.checkUser(email)) {
            emailInputLayout.setError("User already exists with this email");
            emailInputLayout.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake));
            return;
        }

        long result = dbHelper.addUser(username, email, contact, address, selectedGender, selectedCountry, password);
        if (result > 0) {
            Toast.makeText(this, "Registration successful! Welcome to LuxeVista", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}