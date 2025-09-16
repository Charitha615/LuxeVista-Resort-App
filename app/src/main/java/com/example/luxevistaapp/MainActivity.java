package com.example.luxevistaapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure to set the layout first
        setContentView(R.layout.activity_main);

        // Now safely find the root view
        View root = findViewById(R.id.errorPage); // or R.id.main depending on your XML

        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                // handle insets if needed
                return insets;
            });
        }
    }
}
