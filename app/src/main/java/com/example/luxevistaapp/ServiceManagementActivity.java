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

public class ServiceManagementActivity extends AppCompatActivity {

    private ListView lvServices;
    private Button btnAddService;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_management);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadServices();

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceManagementActivity.this, AddEditServiceActivity.class);
                startActivity(intent);
            }
        });

        lvServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int serviceId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

                Intent intent = new Intent(ServiceManagementActivity.this, AddEditServiceActivity.class);
                intent.putExtra("serviceId", serviceId);
                startActivity(intent);
            }
        });

        lvServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int serviceId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String serviceName = cursor.getString(cursor.getColumnIndexOrThrow("service_name"));

                // Delete service with confirmation
                new android.app.AlertDialog.Builder(ServiceManagementActivity.this)
                        .setTitle("Delete Service")
                        .setMessage("Are you sure you want to delete " + serviceName + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int result = dbHelper.deleteService(serviceId);
                            if (result > 0) {
                                Toast.makeText(ServiceManagementActivity.this, "Service deleted successfully", Toast.LENGTH_SHORT).show();
                                loadServices();
                            } else {
                                Toast.makeText(ServiceManagementActivity.this, "Failed to delete service", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    private void initViews() {
        lvServices = findViewById(R.id.lvServices);
        btnAddService = findViewById(R.id.btnAddService);
    }

    private void loadServices() {
        Cursor cursor = dbHelper.getAllServices();

        String[] from = new String[]{"service_name", "service_type", "service_price", "service_duration"};
        int[] to = new int[]{R.id.tvServiceName, R.id.tvServiceType, R.id.tvServicePrice, R.id.tvServiceDuration};

        adapter = new SimpleCursorAdapter(this, R.layout.service_list_item, cursor, from, to, 0);
        lvServices.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
    }
}