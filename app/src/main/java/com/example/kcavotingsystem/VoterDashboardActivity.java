package com.example.kcavotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class VoterDashboardActivity extends AppCompatActivity {

    private TextView activeElectionText;
    private Button voteBtn, logoutBtn;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String ELECTION_COLLECTION = "elections";
    private final String CURRENT_ELECTION_DOC = "currentElection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_dashboard);

        activeElectionText = findViewById(R.id.activeElectionText);
        voteBtn = findViewById(R.id.voteBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadActiveElection();

        voteBtn.setOnClickListener(v -> openVoteActivity());
        logoutBtn.setOnClickListener(v -> logoutUser());
    }

    private void loadActiveElection() {
        db.collection(ELECTION_COLLECTION)
                .document(CURRENT_ELECTION_DOC)
                .addSnapshotListener((snapshot, e) -> {

                    if (snapshot != null && snapshot.exists()) {

                        String title = snapshot.getString("title");
                        Boolean isOpen = snapshot.getBoolean("isOpen");

                        if (title != null) {
                            activeElectionText.setText("Active Election: " + title);
                        }

                        if (isOpen != null && !isOpen) {
                            voteBtn.setEnabled(false);
                            voteBtn.setText("Election Closed");
                        } else {
                            voteBtn.setEnabled(true);
                            voteBtn.setText("Vote Now");
                        }
                    }
                });
    }

    private void openVoteActivity() {
        db.collection(ELECTION_COLLECTION)
                .document(CURRENT_ELECTION_DOC)
                .get()
                .addOnSuccessListener(snapshot -> {

                    Boolean isOpen = snapshot.getBoolean("isOpen");

                    if (isOpen != null && isOpen) {
                        startActivity(new Intent(
                                VoterDashboardActivity.this,
                                VoteActivity.class));
                    } else {
                        Toast.makeText(this,
                                "Election is closed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(VoterDashboardActivity.this, LoginActivity.class));
        finish();
    }
}