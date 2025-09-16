package com.example.luxevistaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etContact, etAddress, etPassword;
    private RadioGroup rgGender;
    private AutoCompleteTextView spCountry;
    private com.google.android.material.button.MaterialButton btnUpdate, btnDeleteAccount;
    private TextView tvEmail;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int userId;
    private String selectedGender = "", selectedCountry = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        initViews();
        setupCountrySpinner();
        loadUserData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmation();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        rgGender = findViewById(R.id.rgGender);
        spCountry = findViewById(R.id.spCountry);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        tvEmail = findViewById(R.id.tvEmail);

        // Password field should be disabled for viewing only
        etPassword.setEnabled(false);

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
        });
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String contact = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));

            etUsername.setText(username);
            etEmail.setText(email);
            tvEmail.setText(email);
            etContact.setText(contact);
            etAddress.setText(address);
            etPassword.setText(password);

            // Set gender
            if (gender.equals("Male")) {
                rgGender.check(R.id.rbMale);
            } else if (gender.equals("Female")) {
                rgGender.check(R.id.rbFemale);
            }
            selectedGender = gender;

            // Set country
            if (country != null && !country.isEmpty()) {
                spCountry.setText(country, false);
                selectedCountry = country;
            }

            cursor.close();
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setMessage("After updating your profile, you will need to log in again for security purposes. Do you want to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUserProfile(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUserProfile(false);
                    }
                })
                .show();
    }

    private void updateUserProfile(boolean shouldLogout) {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty() ||
                selectedGender.isEmpty() || selectedCountry.isEmpty() || selectedCountry.equals("Select Country")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUser(userId, username, email, contact, address, selectedGender, selectedCountry);
        if (result > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            // Update username in shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.apply();

            if (shouldLogout) {
                // Clear login state and logout
                editor.clear();
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone and all your bookings will be cancelled.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        int result = dbHelper.deleteUser(userId);
        if (result > 0) {
            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

            // Clear login state and logout
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
        }
    }
}