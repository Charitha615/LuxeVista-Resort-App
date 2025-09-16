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

public class AdminServiceBookingActivity extends AppCompatActivity {

    private ListView lvServiceBookings;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_booking);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadServiceBookings();

        lvServiceBookings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                showBookingOptions(bookingId, status);
            }
        });
    }

    private void initViews() {
        lvServiceBookings = findViewById(R.id.lvServiceBookings);
    }

    private void loadServiceBookings() {
        Cursor cursor = dbHelper.getAllServiceBookingsWithDetails();

        if (cursor != null && cursor.getCount() > 0) {
            String[] from = new String[]{"username", "service_name", "booking_date", "booking_time", "total_price", "status"};
            int[] to = new int[]{R.id.tvUserName, R.id.tvServiceName, R.id.tvBookingDate, R.id.tvBookingTime, R.id.tvTotalPrice, R.id.tvStatus};

            adapter = new SimpleCursorAdapter(this, R.layout.service_booking_list_item, cursor, from, to, 0);
            lvServiceBookings.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No service bookings found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingOptions(int bookingId, String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Service Booking Management")
                .setMessage("What would you like to do with this booking?");

        if (!status.equals("Cancelled")) {
            builder.setPositiveButton("Cancel Booking", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelServiceBooking(bookingId);
                }
            });
        }

        builder.setNeutralButton("View Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewBookingDetails(bookingId);
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void cancelServiceBooking(int bookingId) {
        int result = dbHelper.cancelServiceBooking(bookingId);
        if (result > 0) {
            Toast.makeText(this, "Service booking cancelled successfully", Toast.LENGTH_SHORT).show();
            loadServiceBookings(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to cancel service booking", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewBookingDetails(int bookingId) {
        Cursor cursor = dbHelper.getServiceBooking(bookingId);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String serviceName = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));
            String bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
            String bookingTime = cursor.getString(cursor.getColumnIndexOrThrow("booking_time"));
            int guests = cursor.getInt(cursor.getColumnIndexOrThrow("guests"));
            double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String specialRequests = cursor.getString(cursor.getColumnIndexOrThrow("special_requests"));

            String details = "Guest: " + username + "\n" +
                    "Service: " + serviceName + "\n" +
                    "Date: " + bookingDate + "\n" +
                    "Time: " + bookingTime + "\n" +
                    "Guests: " + guests + "\n" +
                    "Total: $" + totalPrice + "\n" +
                    "Status: " + status + "\n" +
                    "Special Requests: " + (specialRequests.isEmpty() ? "None" : specialRequests);

            new AlertDialog.Builder(this)
                    .setTitle("Service Booking Details")
                    .setMessage(details)
                    .setPositiveButton("OK", null)
                    .show();

            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServiceBookings();
    }
}