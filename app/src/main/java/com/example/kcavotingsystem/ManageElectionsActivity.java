package com.example.kcavotingsystem;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class ManageElectionsActivity extends AppCompatActivity {

    private EditText titleInput, candidateNameInput, candidateImageUrlInput;
    private RadioGroup positionRadioGroup;
    private Button createTitleBtn, updateTitleBtn, addCandidateBtn, doneBtn;

    private FirebaseFirestore db;

    private final String COLLECTION = "elections";
    private final String DOC = "currentElection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_elections);

        db = FirebaseFirestore.getInstance();

        titleInput = findViewById(R.id.titleInput);
        candidateNameInput = findViewById(R.id.candidateNameInput);
        candidateImageUrlInput = findViewById(R.id.candidateImageUrlInput);
        positionRadioGroup = findViewById(R.id.positionRadioGroup);

        createTitleBtn = findViewById(R.id.createTitleBtn);
        updateTitleBtn = findViewById(R.id.updateTitleBtn);
        addCandidateBtn = findViewById(R.id.addCandidateBtn);
        doneBtn = findViewById(R.id.doneBtn);

        createTitleBtn.setOnClickListener(v -> createTitle());
        updateTitleBtn.setOnClickListener(v -> updateTitle());
        addCandidateBtn.setOnClickListener(v -> addCandidate());
        doneBtn.setOnClickListener(v ->
                Toast.makeText(this, "Candidates added successfully", Toast.LENGTH_SHORT).show());
    }

    private void createTitle() {
        String title = titleInput.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);

        db.collection(COLLECTION).document(DOC).set(data);

        Toast.makeText(this, "Election Created", Toast.LENGTH_SHORT).show();
    }

    private void updateTitle() {
        String title = titleInput.getText().toString().trim();

        db.collection(COLLECTION)
                .document(DOC)
                .update("title", title);

        Toast.makeText(this, "Title Updated", Toast.LENGTH_SHORT).show();
    }

    private void addCandidate() {

        int selectedId = positionRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select Position", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = findViewById(selectedId);
        String position = selected.getText().toString();

        String name = candidateNameInput.getText().toString().trim();
        String imageUrl = candidateImageUrlInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter candidate name", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> candidate = new HashMap<>();
        candidate.put("name", name);
        candidate.put("imageUrl", imageUrl);
        db.collection(COLLECTION)
                .document(DOC)
                .collection("positions")
                .document(position)
                .set(Map.of("name", position));
        DocumentReference candidateRef = db.collection(COLLECTION)
                .document(DOC)
                .collection("positions")
                .document(position)
                .collection("candidates")
                .document(name);

        candidateRef.get().addOnSuccessListener(snapshot -> {

            if (snapshot.exists()) {
                Toast.makeText(this, "Candidate already exists", Toast.LENGTH_SHORT).show();
            } else {
                candidateRef.set(candidate);
                Toast.makeText(this, "Candidate Added", Toast.LENGTH_SHORT).show();
                candidateNameInput.setText("");
                candidateImageUrlInput.setText("");
            }

        });
    }
}
