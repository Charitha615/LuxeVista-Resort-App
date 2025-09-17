package com.example.luxevistaapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoomManagementActivity extends AppCompatActivity {

    private ListView lvRooms;
    private Button btnAddRoom;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);

        dbHelper = new DatabaseHelper(this);
        initViews();

        try {
            loadRooms();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading rooms: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomManagementActivity.this, AddEditRoomActivity.class);
                startActivity(intent);
            }
        });

        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow("_id")); // Changed to _id

                Intent intent = new Intent(RoomManagementActivity.this, AddEditRoomActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        lvRooms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow("_id")); // Changed to _id
                String roomType = cursor.getString(cursor.getColumnIndexOrThrow("room_type"));

                // Delete room with confirmation
                new android.app.AlertDialog.Builder(RoomManagementActivity.this)
                        .setTitle("Delete Room")
                        .setMessage("Are you sure you want to delete " + roomType + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int result = dbHelper.deleteRoom(roomId);
                            if (result > 0) {
                                Toast.makeText(RoomManagementActivity.this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
                                loadRooms();
                            } else {
                                Toast.makeText(RoomManagementActivity.this, "Failed to delete room", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    private void initViews() {
        lvRooms = findViewById(R.id.lvRooms);
        btnAddRoom = findViewById(R.id.btnAddRoom);
    }

    private void loadRooms() {
        Cursor cursor = dbHelper.getAllRooms();

        // Use _id instead of room_id for the adapter
        String[] from = new String[]{ "room_type", "room_price", "room_capacity"};
        int[] to = new int[]{ R.id.tvRoomType, R.id.tvRoomPrice, R.id.tvRoomCapacity};

        adapter = new SimpleCursorAdapter(this, R.layout.room_list_item, cursor, from, to, 0);
        lvRooms.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }
}