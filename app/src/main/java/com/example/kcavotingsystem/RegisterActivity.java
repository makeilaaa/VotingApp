package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameInput, registrationNumberInput, passwordInput;
    private Button registerBtn, goToLoginBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameInput = findViewById(R.id.fullNameInput);
        registrationNumberInput = findViewById(R.id.registrationNumberInput);
        passwordInput = findViewById(R.id.passwordInput);

        registerBtn = findViewById(R.id.registerBtn);
        goToLoginBtn = findViewById(R.id.goToLoginBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> registerUser());
        goToLoginBtn.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String fullName = fullNameInput.getText().toString().trim();
        String regNumber = registrationNumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) { fullNameInput.setError("Full Name required"); return; }
        if (TextUtils.isEmpty(regNumber)) { registrationNumberInput.setError("Registration Number required"); return; }
        if (TextUtils.isEmpty(password)) { passwordInput.setError("Password required"); return; }
        if (password.length() < 6) { passwordInput.setError("Password must be at least 6 characters"); return; }

        // Create dummy email for Firebase Auth
        String email = regNumber + "@kca.edu";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // ✅ Write user info to Firestore
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("regNumber", regNumber);
                        userMap.put("role", "voter"); // default role = voter

                        db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}