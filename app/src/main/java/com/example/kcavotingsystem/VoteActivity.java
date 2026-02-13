package com.example.kcavotingsystem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VoteActivity extends AppCompatActivity {

    private LinearLayout positionsContainer;
    private Button btnSubmitVote;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String COLLECTION = "elections";
    private final String DOC = "currentElection";

    // Map to store one RadioGroup per position
    private Map<String, RadioGroup> positionGroups = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        positionsContainer = findViewById(R.id.positionsContainer);
        btnSubmitVote = findViewById(R.id.btnSubmitVote);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        checkIfAlreadyVoted();
        loadPositions();

        btnSubmitVote.setOnClickListener(v -> submitVote());
    }

    // ================= CHECK DOUBLE VOTING =================
    private void checkIfAlreadyVoted() {

        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection(COLLECTION)
                .document(DOC)
                .collection("votes")
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Toast.makeText(this, "You have already voted!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, ThankYouActivity.class));
                        finish();
                    }
                });
    }

    private void checkElectionStatusAndLoad() {
        db.collection("elections")
                .document("currentElection")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String status = snapshot.getString("status");
                        if ("closed".equals(status)) {
                            Toast.makeText(this,
                                    "Voting has ended for this election!",
                                    Toast.LENGTH_LONG).show();
                            finish();  // exit the voting page
                        } else {
                            loadPositions();  // election is open
                        }
                    }
                });
    }

    // ================= LOAD POSITIONS =================
    private void loadPositions() {

        positionsContainer.removeAllViews();
        positionGroups.clear();

        db.collection(COLLECTION)
                .document(DOC)
                .collection("positions")
                .get()
                .addOnSuccessListener(positionSnapshots -> {

                    for (DocumentSnapshot positionDoc : positionSnapshots) {

                        String positionName = positionDoc.getId();

                        // ===== POSITION TITLE =====
                        TextView title = new TextView(this);
                        title.setText(positionName);
                        title.setTextSize(20f);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setTextColor(Color.BLACK);
                        title.setPadding(0, 30, 0, 10);
                        title.setGravity(Gravity.CENTER);
                        positionsContainer.addView(title);

                        // ===== RADIO GROUP =====
                        RadioGroup group = new RadioGroup(this);
                        group.setOrientation(RadioGroup.VERTICAL);
                        group.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));

                        positionsContainer.addView(group);
                        positionGroups.put(positionName, group);

                        loadCandidates(positionName, group);
                    }

                    // Add submit button at end
                    if (btnSubmitVote.getParent() != null) {
                        ((ViewGroup) btnSubmitVote.getParent()).removeView(btnSubmitVote);
                    }
                    positionsContainer.addView(btnSubmitVote);
                });
    }

    // ================= LOAD CANDIDATES =================
    private void loadCandidates(String positionName, RadioGroup group) {

        db.collection(COLLECTION)
                .document(DOC)
                .collection("positions")
                .document(positionName)
                .collection("candidates")
                .get()
                .addOnSuccessListener(candidateSnapshots -> {

                    group.removeAllViews();

                    for (DocumentSnapshot candidateDoc : candidateSnapshots) {

                        String candidateName = candidateDoc.getString("name");
                        String candidateId = candidateDoc.getId();
                        String imageUrl = candidateDoc.getString("imageUrl");

                        // Create RadioButton (direct child of RadioGroup)
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText(candidateName);
                        radioButton.setTextColor(Color.BLACK);
                        radioButton.setTextSize(16f);
                        radioButton.setTag(candidateId);
                        radioButton.setPadding(0, 20, 0, 20);

                        // Load image as drawable on the left side
                        ImageView tempImage = new ImageView(this);
                        Glide.with(this)
                                .asBitmap()
                                .load(imageUrl)
                                .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
                                    @Override
                                    public void onResourceReady(android.graphics.Bitmap resource,
                                                                com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> transition) {

                                        android.graphics.drawable.Drawable drawable =
                                                new android.graphics.drawable.BitmapDrawable(getResources(), resource);

                                        drawable.setBounds(0, 0, 150, 150);

                                        radioButton.setCompoundDrawables(drawable, null, null, null);
                                        radioButton.setCompoundDrawablePadding(40);
                                    }

                                    @Override
                                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                                    }
                                });

                        // Add directly to RadioGroup (THIS keeps single selection working)
                        group.addView(radioButton);
                    }
                });
    }

    // ================= SUBMIT VOTE =================
    private void submitVote() {

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, String> voteMap = new HashMap<>();

        for (String position : positionGroups.keySet()) {

            RadioGroup group = positionGroups.get(position);
            int selectedId = group.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please vote for all positions", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            voteMap.put(position, selected.getText().toString());
        }

        db.collection(COLLECTION)
                .document(DOC)
                .collection("votes")
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        Toast.makeText(this, "You have already voted!", Toast.LENGTH_LONG).show();

                    } else {

                        db.collection(COLLECTION)
                                .document(DOC)
                                .collection("votes")
                                .document(userId)
                                .set(voteMap)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this, "Vote Submitted Successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(VoteActivity.this, ThankYouActivity.class));
                                    finish();
                                });
                    }
                });
    }
}