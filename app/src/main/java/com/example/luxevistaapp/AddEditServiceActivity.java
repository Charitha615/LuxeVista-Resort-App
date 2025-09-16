package com.example.luxevistaapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditServiceActivity extends AppCompatActivity {

    private EditText etServiceName, etServiceDescription, etServicePrice, etServiceDuration;
    private Spinner spServiceType;
    private CheckBox cbServiceAvailable;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int serviceId = -1; // -1 means new service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_service);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupServiceTypeSpinner();

        // Check if we're editing an existing service
        Intent intent = getIntent();
        if (intent.hasExtra("serviceId")) {
            serviceId = intent.getIntExtra("serviceId", -1);
            loadServiceData(serviceId);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveService();
            }
        });
    }

    private void initViews() {
        etServiceName = findViewById(R.id.etServiceName);
        etServiceDescription = findViewById(R.id.etServiceDescription);
        etServicePrice = findViewById(R.id.etServicePrice);
        etServiceDuration = findViewById(R.id.etServiceDuration);
        spServiceType = findViewById(R.id.spServiceType);
        cbServiceAvailable = findViewById(R.id.cbServiceAvailable);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupServiceTypeSpinner() {
        String[] serviceTypes = {"Spa", "Dining", "Poolside", "Beach Tour", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, serviceTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServiceType.setAdapter(adapter);
    }

    private void loadServiceData(int serviceId) {
        Cursor cursor = dbHelper.getService(serviceId);
        if (cursor != null && cursor.moveToFirst()) {
            etServiceName.setText(cursor.getString(cursor.getColumnIndexOrThrow("service_name")));
            etServiceDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("service_description")));
            etServicePrice.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("service_price"))));
            etServiceDuration.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("service_duration"))));
            cbServiceAvailable.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("service_available")) == 1);

            // Set service type
            String serviceType = cursor.getString(cursor.getColumnIndexOrThrow("service_type"));
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spServiceType.getAdapter();
            int position = adapter.getPosition(serviceType);
            if (position >= 0) {
                spServiceType.setSelection(position);
            }

            cursor.close();
        }
    }

    private void saveService() {
        String name = etServiceName.getText().toString().trim();
        String description = etServiceDescription.getText().toString().trim();
        String priceStr = etServicePrice.getText().toString().trim();
        String durationStr = etServiceDuration.getText().toString().trim();
        String type = spServiceType.getSelectedItem().toString();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int duration = Integer.parseInt(durationStr);
        boolean available = cbServiceAvailable.isChecked();

        long result;
        if (serviceId == -1) {
            // Add new service
            result = dbHelper.addService(name, description, price, type, available, duration);
        } else {
            // Update existing service
            result = dbHelper.updateService(serviceId, name, description, price, type, available, duration);
        }

        if (result > 0) {
            Toast.makeText(this, "Service saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save service", Toast.LENGTH_SHORT).show();
        }
    }
}