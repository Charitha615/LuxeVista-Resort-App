package com.example.luxevistaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GuestDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserId;
    private Button btnViewProfile, btnBookRoom, btnMyBookings, btnLogout,btnBookServices, btnMyServiceBookings;;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE);

        // Get user info from shared preferences
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", "Guest");

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserId = findViewById(R.id.tvUserId);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnBookRoom = findViewById(R.id.btnBookRoom);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnLogout = findViewById(R.id.btnLogout);
        btnBookServices = findViewById(R.id.btnBookServices);
        btnMyServiceBookings = findViewById(R.id.btnMyServiceBookings);

        tvWelcome.setText("Welcome, " + username);
        tvUserId.setText("User ID: " + userId);
    }

    private void setupClickListeners() {
        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnBookRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestDashboardActivity.this, RoomBookingActivity.class);
                startActivity(intent);
            }
        });

        btnMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show user's bookings
                Cursor cursor = dbHelper.getUserBookings(userId);
                if (cursor != null && cursor.getCount() > 0) {
                    StringBuilder bookings = new StringBuilder();
                    while (cursor.moveToNext()) {
                        String roomType = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));
                        String checkIn = cursor.getString(cursor.getColumnIndexOrThrow("check_in"));
                        String checkOut = cursor.getString(cursor.getColumnIndexOrThrow("check_out"));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
                        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                        bookings.append(roomType).append(": ")
                                .append(checkIn).append(" to ").append(checkOut)
                                .append(" - $").append(price)
                                .append(" (").append(status).append(")\n\n");
                    }
                    cursor.close();

                    new android.app.AlertDialog.Builder(GuestDashboardActivity.this)
                            .setTitle("My Bookings")
                            .setMessage(bookings.toString())
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(GuestDashboardActivity.this, "No bookings found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear login state
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(GuestDashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBookServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestDashboardActivity.this, ServiceBookingActivity.class);
                startActivity(intent);
            }
        });

        btnMyServiceBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestDashboardActivity.this, UserServiceBookingActivity.class);
                startActivity(intent);
            }
        });
    }
}