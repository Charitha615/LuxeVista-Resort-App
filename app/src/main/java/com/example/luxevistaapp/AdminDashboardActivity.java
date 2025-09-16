package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageRooms, btnManageServices, btnManageServiceBookings, btnLogout,btnManageBookings;;
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
        btnManageServiceBookings = findViewById(R.id.btnManageServiceBookings);
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageBookings = findViewById(R.id.btnManageBookings);


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

        btnManageBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminBookingActivity.class);
                startActivity(intent);
            }
        });

        btnManageServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ServiceManagementActivity.class);
                startActivity(intent);
            }
        });

        // Add to setupClickListeners() method
        btnManageServiceBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminServiceBookingActivity.class);
                startActivity(intent);
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