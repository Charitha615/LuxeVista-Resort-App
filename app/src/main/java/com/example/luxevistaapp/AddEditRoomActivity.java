package com.example.luxevistaapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditRoomActivity extends AppCompatActivity {

    private EditText etRoomType, etRoomDescription, etRoomPrice, etRoomCapacity;
    private CheckBox cbRoomAvailable;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int roomId = -1; // -1 means new room

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);

        dbHelper = new DatabaseHelper(this);
        initViews();

        // Check if we're editing an existing room
        Intent intent = getIntent();
        if (intent.hasExtra("roomId")) {
            roomId = intent.getIntExtra("roomId", -1);
            loadRoomData(roomId);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoom();
            }
        });
    }

    private void initViews() {
        etRoomType = findViewById(R.id.etRoomType);
        etRoomDescription = findViewById(R.id.etRoomDescription);
        etRoomPrice = findViewById(R.id.etRoomPrice);
        etRoomCapacity = findViewById(R.id.etRoomCapacity);
        cbRoomAvailable = findViewById(R.id.cbRoomAvailable);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadRoomData(int roomId) {
        Cursor cursor = dbHelper.getRoom(roomId);
        if (cursor != null && cursor.moveToFirst()) {
            etRoomType.setText(cursor.getString(cursor.getColumnIndexOrThrow("room_type")));
            etRoomDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("room_description")));
            etRoomPrice.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("room_price"))));
            etRoomCapacity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("room_capacity"))));
            cbRoomAvailable.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("room_available")) == 1);
        }
    }

    private void saveRoom() {
        String type = etRoomType.getText().toString().trim();
        String description = etRoomDescription.getText().toString().trim();
        String priceStr = etRoomPrice.getText().toString().trim();
        String capacityStr = etRoomCapacity.getText().toString().trim();

        if (type.isEmpty() || description.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int capacity = Integer.parseInt(capacityStr);
        boolean available = cbRoomAvailable.isChecked();

        long result;
        if (roomId == -1) {
            // Add new room
            result = dbHelper.addRoom(type, description, price, capacity, available, "");
        } else {
            // Update existing room
            result = dbHelper.updateRoom(roomId, type, description, price, capacity, available, "");
        }

        if (result > 0) {
            Toast.makeText(this, "Room saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save room", Toast.LENGTH_SHORT).show();
        }
    }
}