package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText registrationNumberInput, passwordInput;
    private Button loginBtn, goToRegisterBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registrationNumberInput = findViewById(R.id.registrationNumberInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        goToRegisterBtn = findViewById(R.id.goToRegisterBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());
        goToRegisterBtn.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String regNumber = registrationNumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(regNumber)) { registrationNumberInput.setError("Registration Number required"); return; }
        if (TextUtils.isEmpty(password)) { passwordInput.setError("Password required"); return; }

        String email = regNumber + "@kca.edu";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        // ✅ Fetch user role from Firestore
                        db.collection("users").document(userId).get()
                                .addOnSuccessListener(DocumentSnapshot -> {
                                    if (DocumentSnapshot.exists()) {
                                        String role = DocumentSnapshot.getString("role");

                                        if (role != null && role.equals("admin")) {
                                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                        } else {
                                            startActivity(new Intent(LoginActivity.this, VoterDashboardActivity.class));
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User data missing in Firestore", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}