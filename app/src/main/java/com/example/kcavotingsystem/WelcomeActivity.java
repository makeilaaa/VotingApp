package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView logoImage = findViewById(R.id.logoImage);
        // The launcher/logo is displayed in XML

        // Wait for 2 seconds, then go to LoginChoiceActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginChoiceActivity.class);
            startActivity(intent);
            finish(); // prevent back button returning to splash
        }, SPLASH_DURATION);
    }
}