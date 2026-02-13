package com.example.kcavotingsystem;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;
    private FirebaseFirestore db;

    private final String COLLECTION = "elections";
    private final String DOC = "currentElection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsContainer = findViewById(R.id.resultsContainer);
        db = FirebaseFirestore.getInstance();

        loadResults();
    }

    private void loadResults() {

        db.collection(COLLECTION)
                .document(DOC)
                .collection("votes")   // ✅ Correct path
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    resultsContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No votes submitted yet");
                        empty.setTextSize(22);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 300, 0, 0);
                        resultsContainer.addView(empty);
                        return;
                    }

                    // Map<Position, Map<Candidate, VoteCount>>
                    Map<String, Map<String, Integer>> tallyMap = new HashMap<>();

                    // ================= COUNT VOTES =================
                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        Map<String, Object> voteData = doc.getData();

                        for (String position : voteData.keySet()) {

                            String candidate = voteData.get(position).toString();

                            tallyMap.putIfAbsent(position, new HashMap<>());

                            Map<String, Integer> candidateMap = tallyMap.get(position);

                            candidateMap.put(candidate,
                                    candidateMap.getOrDefault(candidate, 0) + 1);
                        }
                    }

                    // ================= DISPLAY RESULTS =================
                    for (String position : tallyMap.keySet()) {

                        // ===== Position Title =====
                        TextView positionTitle = new TextView(this);
                        positionTitle.setText(position);
                        positionTitle.setTextSize(26);
                        positionTitle.setTypeface(null, Typeface.BOLD);
                        positionTitle.setGravity(Gravity.CENTER);
                        positionTitle.setPadding(0, 60, 0, 30);
                        resultsContainer.addView(positionTitle);

                        Map<String, Integer> candidates = tallyMap.get(position);

                        ArrayList<BarEntry> entries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<>();

                        int index = 0;
                        for (String candidate : candidates.keySet()) {

                            int votes = candidates.get(candidate);

                            entries.add(new BarEntry(index, votes));
                            labels.add(candidate);

                            index++;
                        }

                        // ===== Dataset =====
                        BarDataSet dataSet = new BarDataSet(entries, "Votes");
                        dataSet.setValueTextSize(18f);
                        dataSet.setValueTextColor(Color.BLACK);

                        // Alternate Blue & Yellow
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int i = 0; i < entries.size(); i++) {
                            if (i % 2 == 0) {
                                colors.add(Color.parseColor("#2196F3")); // Blue
                            } else {
                                colors.add(Color.parseColor("#FFC107")); // Yellow
                            }
                        }
                        dataSet.setColors(colors);

                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);

                        // ===== Chart =====
                        BarChart chart = new BarChart(this);

                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1000);

                        params.setMargins(0, 20, 0, 60);
                        chart.setLayoutParams(params);

                        chart.setData(barData);
                        chart.setFitBars(true);
                        chart.getDescription().setEnabled(false);
                        chart.getAxisRight().setEnabled(false);

                        // White Background
                        chart.setBackgroundColor(Color.WHITE);

                        // X Axis
                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                        xAxis.setGranularity(1f);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextSize(16f);
                        xAxis.setLabelRotationAngle(-20f);

                        chart.getAxisLeft().setTextSize(16f);
                        chart.getLegend().setTextSize(16f);

                        chart.animateY(1200);
                        chart.invalidate();

                        resultsContainer.addView(chart);

                        // ===== Numeric Tally Section =====
                        for (String candidate : candidates.keySet()) {

                            int votes = candidates.get(candidate);

                            TextView numeric = new TextView(this);
                            numeric.setText(candidate + " : " + votes + " votes");
                            numeric.setTextSize(20);
                            numeric.setGravity(Gravity.CENTER);
                            numeric.setPadding(0, 10, 0, 10);

                            resultsContainer.addView(numeric);
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    TextView error = new TextView(this);
                    error.setText("Failed to load results");
                    error.setTextSize(22);
                    error.setGravity(Gravity.CENTER);
                    error.setPadding(0, 300, 0, 0);

                    resultsContainer.addView(error);
                });
    }
}
