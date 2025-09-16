package com.example.luxevistaapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RoomBookingActivity extends AppCompatActivity {

    private Spinner spRoomType;
    private EditText etCheckIn, etCheckOut, etGuests;
    private TextView tvPrice, tvTotalPrice;
    private Button btnCalculate, btnBook;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int userId, selectedRoomId;
    private double roomPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LuxeVistaPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        initViews();
        loadAvailableRooms();

        // Set default dates (today and tomorrow)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etCheckIn.setText(sdf.format(new Date()));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        etCheckOut.setText(sdf.format(calendar.getTime()));

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotalPrice();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookRoom();
            }
        });
    }

    private void initViews() {
        spRoomType = findViewById(R.id.spRoomType);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        etGuests = findViewById(R.id.etGuests);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnBook = findViewById(R.id.btnBook);
    }

    private void loadAvailableRooms() {
        Cursor cursor = dbHelper.getAllRooms();
        if (cursor != null && cursor.getCount() > 0) {
            String[] roomNames = new String[cursor.getCount()];
            final int[] roomIds = new int[cursor.getCount()];
            final double[] roomPrices = new double[cursor.getCount()];

            int i = 0;
            while (cursor.moveToNext()) {
                roomIds[i] = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                roomNames[i] = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));
                roomPrices[i] = cursor.getDouble(cursor.getColumnIndexOrThrow("room_price"));
                i++;
            }
            cursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, roomNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRoomType.setAdapter(adapter);

            spRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedRoomId = roomIds[position];
                    roomPrice = roomPrices[position];
                    tvPrice.setText("Price per night: $" + roomPrice);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void calculateTotalPrice() {
        try {
            String checkInStr = etCheckIn.getText().toString();
            String checkOutStr = etCheckOut.getText().toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date checkIn = sdf.parse(checkInStr);
            Date checkOut = sdf.parse(checkOutStr);

            long diff = checkOut.getTime() - checkIn.getTime();
            int nights = (int) (diff / (1000 * 60 * 60 * 24));

            if (nights <= 0) {
                Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = nights * roomPrice;
            tvTotalPrice.setText("Total: $" + totalPrice + " for " + nights + " nights");

        } catch (Exception e) {
            Toast.makeText(this, "Please enter valid dates (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
        }
    }

    private void bookRoom() {
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();
        String guestsStr = etGuests.getText().toString().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty() || guestsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int guests = Integer.parseInt(guestsStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date checkInDate = sdf.parse(checkIn);
            Date checkOutDate = sdf.parse(checkOut);

            long diff = checkOutDate.getTime() - checkInDate.getTime();
            int nights = (int) (diff / (1000 * 60 * 60 * 24));
            double totalPrice = nights * roomPrice;

            long result = dbHelper.addBooking(userId, selectedRoomId, checkIn, checkOut,
                    guests, totalPrice, "Confirmed");

            if (result > 0) {
                Toast.makeText(this, "Room booked successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to book room", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Please enter valid information", Toast.LENGTH_SHORT).show();
        }
    }
}