package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageRooms, btnManageServices, btnViewBookings, btnLogout;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnManageRooms = findViewById(R.id.btnManageRooms);
        btnManageServices = findViewById(R.id.btnManageServices);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);

        tvWelcome.setText("Welcome, Admin");
    }

    private void setupClickListeners() {
        btnManageRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, RoomManagementActivity.class);
                startActivity(intent);
            }
        });

        btnManageServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Service management coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminDashboardActivity.this, "Booking view coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear login state
                getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}