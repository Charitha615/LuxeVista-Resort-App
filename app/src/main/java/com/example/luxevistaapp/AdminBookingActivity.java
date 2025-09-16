package com.example.luxevistaapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminBookingActivity extends AppCompatActivity {

    private ListView lvBookings;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadBookings();

        lvBookings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                if (!status.equals("Cancelled")) {
                    showBookingOptions(bookingId);
                }
            }
        });
    }

    private void initViews() {
        lvBookings = findViewById(R.id.lvBookings);
    }

    private void loadBookings() {
        Cursor cursor = dbHelper.getAllBookingsWithDetails();

        String[] from = new String[]{"username", "room_type", "check_in", "check_out", "total_price", "status"};
        int[] to = new int[]{R.id.tvUserName, R.id.tvRoomType, R.id.tvCheckIn, R.id.tvCheckOut, R.id.tvTotalPrice, R.id.tvStatus};

        adapter = new SimpleCursorAdapter(this, R.layout.booking_list_item, cursor, from, to, 0);
        lvBookings.setAdapter(adapter);
    }

    private void showBookingOptions(int bookingId) {
        new AlertDialog.Builder(this)
                .setTitle("Booking Management")
                .setMessage("What would you like to do with this booking?")
                .setPositiveButton("Cancel Booking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelBooking(bookingId);
                    }
                })
                .setNegativeButton("View Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewBookingDetails(bookingId);
                    }
                })
                .setNeutralButton("Close", null)
                .show();
    }

    private void cancelBooking(int bookingId) {
        int result = dbHelper.cancelBooking(bookingId);
        if (result > 0) {
            Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
            loadBookings(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewBookingDetails(int bookingId) {
        Cursor cursor = dbHelper.getBookingById(bookingId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String roomType = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));
            String checkIn = cursor.getString(cursor.getColumnIndexOrThrow("check_in"));
            String checkOut = cursor.getString(cursor.getColumnIndexOrThrow("check_out"));
            int guests = cursor.getInt(cursor.getColumnIndexOrThrow("guests"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            String details = "Guest: " + username + "\n" +
                    "Room: " + roomType + "\n" +
                    "Check-in: " + checkIn + "\n" +
                    "Check-out: " + checkOut + "\n" +
                    "Guests: " + guests + "\n" +
                    "Total: $" + totalPrice + "\n" +
                    "Status: " + status;

            new AlertDialog.Builder(this)
                    .setTitle("Booking Details")
                    .setMessage(details)
                    .setPositiveButton("OK", null)
                    .show();

            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}