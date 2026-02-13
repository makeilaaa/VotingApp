package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        // Optional: automatically return to voter dashboard after 4 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(ThankYouActivity.this, VoterDashboardActivity.class));
            finish();
        }, 4000); // 4000ms = 4 seconds
    }
}