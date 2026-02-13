package com.example.kcavotingsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ManageCandidatesActivity extends AppCompatActivity {

    private EditText candidateNameInput, candidateImageUrlInput;
    private Button addCandidateBtn;
    private LinearLayout candidatesContainer;

    private FirebaseFirestore db;

    private String electionId = "currentElection";
    private String positionId; // received from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_candidates);

        // 🔹 Bind views
        candidateNameInput = findViewById(R.id.candidateNameInput);
        candidateImageUrlInput = findViewById(R.id.candidateImageUrlInput);
        addCandidateBtn = findViewById(R.id.addCandidateBtn);
        candidatesContainer = findViewById(R.id.candidatesContainer);

        db = FirebaseFirestore.getInstance();

        // 🔹 Get positionId from previous activity
        positionId = getIntent().getStringExtra("positionId");

        if (positionId == null) {
            Toast.makeText(this, "Position not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 🔹 Add candidate
        addCandidateBtn.setOnClickListener(v -> addCandidate());

        // 🔹 Load existing candidates
        loadCandidates();
    }

    // ================= ADD CANDIDATE =================

    private void addCandidate() {
        String name = candidateNameInput.getText().toString().trim();
        String imageUrl = candidateImageUrlInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            candidateNameInput.setError("Candidate name required");
            return;
        }

        Candidate candidate = new Candidate(name, imageUrl);

        db.collection("elections")
                .document(electionId)
                .collection("positions")
                .document(positionId)
                .collection("candidates")
                .add(candidate)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Candidate added", Toast.LENGTH_SHORT).show();
                    candidateNameInput.setText("");
                    candidateImageUrlInput.setText("");
                    loadCandidates();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ================= LOAD CANDIDATES =================

    private void loadCandidates() {
        candidatesContainer.removeAllViews();

        db.collection("elections")
                .document(electionId)
                .collection("positions")
                .document(positionId)
                .collection("candidates")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText("No candidates added yet");
                        candidatesContainer.addView(empty);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        Candidate candidate = doc.toObject(Candidate.class);

                        LinearLayout row = new LinearLayout(this);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setPadding(0, 16, 0, 16);

                        TextView nameText = new TextView(this);
                        nameText.setText(candidate.name);
                        nameText.setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1)
                        );

                        Button deleteBtn = new Button(this);
                        deleteBtn.setText("Delete");

                        deleteBtn.setOnClickListener(v ->
                                deleteCandidate(doc.getId())
                        );

                        row.addView(nameText);
                        row.addView(deleteBtn);

                        candidatesContainer.addView(row);
                    }
                });
    }

    // ================= DELETE CANDIDATE =================

    private void deleteCandidate(String candidateId) {
        db.collection("elections")
                .document(electionId)
                .collection("positions")
                .document(positionId)
                .collection("candidates")
                .document(candidateId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Candidate deleted", Toast.LENGTH_SHORT).show();
                    loadCandidates();
                });
    }

    // ================= FIRESTORE MODEL =================

    static class Candidate {
        public String name;
        public String imageUrl;

        public Candidate() {
            // Required empty constructor
        }

        public Candidate(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }
}
