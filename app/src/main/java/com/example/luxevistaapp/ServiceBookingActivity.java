package com.example.luxevistaapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceBookingActivity extends AppCompatActivity {

    private Spinner spServiceType, spService, spTimeSlot;
    private EditText etBookingDate, etGuests, etSpecialRequests;
    private TextView tvServiceDescription, tvServicePrice, tvTotalPrice;
    private Button btnCheckAvailability, btnBookService;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int userId, selectedServiceId;
    private double servicePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_booking);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        initViews();
        setupServiceTypeSpinner();

        // Set default date (today)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etBookingDate.setText(sdf.format(new Date()));

        // Add text change listener to calculate total price
        etGuests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalPrice();
            }
        });

        btnCheckAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAvailability();
            }
        });

        btnBookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookService();
            }
        });
    }

    private void initViews() {
        spServiceType = findViewById(R.id.spServiceType);
        spService = findViewById(R.id.spService);
        spTimeSlot = findViewById(R.id.spTimeSlot);
        etBookingDate = findViewById(R.id.etBookingDate);
        etGuests = findViewById(R.id.etGuests);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        tvServiceDescription = findViewById(R.id.tvServiceDescription);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnBookService = findViewById(R.id.btnBookService);
    }

    private void calculateTotalPrice() {
        String guestsStr = etGuests.getText().toString().trim();
        if (!guestsStr.isEmpty()) {
            try {
                int guests = Integer.parseInt(guestsStr);
                double totalPrice = servicePrice * guests;
                tvTotalPrice.setText(String.format(Locale.getDefault(), "Total: LKR %.2f", totalPrice));
            } catch (NumberFormatException e) {
                tvTotalPrice.setText("Total: LKR 0.00");
            }
        } else {
            tvTotalPrice.setText("Total: LKR 0.00");
        }
    }

    private void setupServiceTypeSpinner() {
        String[] serviceTypes = {"All", "Spa", "Dining", "Poolside", "Beach Tour", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, serviceTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServiceType.setAdapter(adapter);

        spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                loadServices(selectedType.equals("All") ? null : selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadServices(String serviceType) {
        Cursor cursor;
        if (serviceType == null) {
            cursor = dbHelper.getAllServices();
        } else {
            cursor = dbHelper.getReadableDatabase().query("services", null,
                    "service_type=?", new String[]{serviceType}, null, null, "service_name");
        }

        if (cursor != null && cursor.getCount() > 0) {
            List<String> serviceNames = new ArrayList<>();
            final List<Integer> serviceIds = new ArrayList<>();
            final List<Double> servicePrices = new ArrayList<>();
            final List<String> serviceDescriptions = new ArrayList<>();

            while (cursor.moveToNext()) {
                serviceIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                serviceNames.add(cursor.getString(cursor.getColumnIndexOrThrow("service_name")));
                servicePrices.add(cursor.getDouble(cursor.getColumnIndexOrThrow("service_price")));
                serviceDescriptions.add(cursor.getString(cursor.getColumnIndexOrThrow("service_description")));
            }
            cursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, serviceNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spService.setAdapter(adapter);

            spService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedServiceId = serviceIds.get(position);
                    servicePrice = servicePrices.get(position);
                    tvServiceDescription.setText(serviceDescriptions.get(position));
                    tvServicePrice.setText(String.format(Locale.getDefault(), "Price: $%.2f", servicePrice));
                    calculateTotalPrice(); // Update total when service changes
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void checkAvailability() {
        String date = etBookingDate.getText().toString().trim();
        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate available time slots (every 30 minutes from 9 AM to 9 PM)
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 9; hour < 21; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeSlots.add(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        }

        // Remove booked time slots
        Cursor bookedSlots = dbHelper.getAvailableTimeSlots(selectedServiceId, date);
        if (bookedSlots != null) {
            while (bookedSlots.moveToNext()) {
                String bookedTime = bookedSlots.getString(bookedSlots.getColumnIndexOrThrow("booking_time"));
                timeSlots.remove(bookedTime);
            }
            bookedSlots.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeSlot.setAdapter(adapter);

        if (timeSlots.isEmpty()) {
            Toast.makeText(this, "No available time slots for selected date", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Available time slots loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void bookService() {
        String date = etBookingDate.getText().toString().trim();
        String time = spTimeSlot.getSelectedItem() != null ? spTimeSlot.getSelectedItem().toString() : "";
        String guestsStr = etGuests.getText().toString().trim();
        String specialRequests = etSpecialRequests.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || guestsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int guests = Integer.parseInt(guestsStr);
            double totalPrice = servicePrice * guests;

            long result = dbHelper.addServiceBooking(userId, selectedServiceId, date, time,
                    guests, totalPrice, "Confirmed", specialRequests);

            if (result > 0) {
                Toast.makeText(this, "Service booked successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to book service", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Please enter valid information", Toast.LENGTH_SHORT).show();
        }
    }
}