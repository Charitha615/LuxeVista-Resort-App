package com.example.luxevistaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GuestDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUsername, tvUserId;
    private LinearLayout cardProfile, cardBookRoom, cardMyBookings, cardBookServices, cardServiceBookings, cardLogout;
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
        tvUsername = findViewById(R.id.tvUsername);
        tvUserId = findViewById(R.id.tvUserId);

        cardProfile = findViewById(R.id.cardProfile);
        cardBookRoom = findViewById(R.id.cardBookRoom);
        cardMyBookings = findViewById(R.id.cardMyBookings);
        cardBookServices = findViewById(R.id.cardBookServices);
        cardServiceBookings = findViewById(R.id.cardServiceBookings);
        cardLogout = findViewById(R.id.cardLogout);

        tvUsername.setText(username);
        tvUserId.setText("User ID: " + userId);
    }

    private void setupClickListeners() {
        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        cardBookRoom.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, RoomBookingActivity.class);
            startActivity(intent);
        });

        cardMyBookings.setOnClickListener(v -> {
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
        });

        cardBookServices.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, ServiceBookingActivity.class);
            startActivity(intent);
        });

        cardServiceBookings.setOnClickListener(v -> {
            Intent intent = new Intent(GuestDashboardActivity.this, UserServiceBookingActivity.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(GuestDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
