package com.example.kcavotingsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView electionStatusText;
    private Button openElectionBtn, closeElectionBtn, manageElectionsBtn, viewResultsBtn;

    private FirebaseFirestore db;

    private final String ELECTION_COLLECTION = "elections";
    private final String CURRENT_ELECTION_DOC = "currentElection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        electionStatusText = findViewById(R.id.electionStatusText);
        openElectionBtn = findViewById(R.id.openElectionBtn);
        closeElectionBtn = findViewById(R.id.closeElectionBtn);
        manageElectionsBtn = findViewById(R.id.manageElectionsBtn);
        viewResultsBtn = findViewById(R.id.viewResultsBtn);

        db = FirebaseFirestore.getInstance();

        loadElectionStatus();

        openElectionBtn.setOnClickListener(v -> openElection());
        closeElectionBtn.setOnClickListener(v -> closeElection());

        manageElectionsBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(
                    AdminDashboardActivity.this,
                    ManageElectionsActivity.class));
        });

        viewResultsBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(
                    AdminDashboardActivity.this,
                    ResultsActivity.class));
        });
    }

    private void loadElectionStatus() {
        db.collection(ELECTION_COLLECTION)
                .document(CURRENT_ELECTION_DOC)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Boolean isOpen = snapshot.getBoolean("isOpen");
                        if (isOpen != null && isOpen) {
                            electionStatusText.setText("Election Status: Open");
                        } else {
                            electionStatusText.setText("Election Status: Closed");
                        }
                    } else {
                        electionStatusText.setText("Election Status: None");
                    }
                })
                .addOnFailureListener(e -> {
                    electionStatusText.setText("Election Status: Unknown");
                });
    }

    private void openElection() {
        db.collection(ELECTION_COLLECTION)
                .document(CURRENT_ELECTION_DOC)
                .update("isOpen", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Election is now Open", Toast.LENGTH_SHORT).show();
                    electionStatusText.setText("Election Status: Open");
                });
    }

    private void closeElection() {
        db.collection(ELECTION_COLLECTION)
                .document(CURRENT_ELECTION_DOC)
                .update("isOpen", false)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Election Closed Successfully", Toast.LENGTH_SHORT).show();
                    electionStatusText.setText("Election Status: Closed");
                });
    }
}