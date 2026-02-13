package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginChoiceActivity extends AppCompatActivity {

    private Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginChoiceActivity.this, LoginActivity.class));
        });

        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginChoiceActivity.this, RegisterActivity.class));
        });
    }
}