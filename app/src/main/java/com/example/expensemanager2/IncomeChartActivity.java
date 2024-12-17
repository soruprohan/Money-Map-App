package com.example.expensemanager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Pie;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class IncomeChartActivity extends AppCompatActivity {

    private static final String TAG = "IncomeChartActivity";
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    AnyChartView anyChartView;
    List<DataEntry> dataEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_chart);

        anyChartView = findViewById(R.id.any_chart_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        dataEntries = new ArrayList<>();

        // Load income data from Firestore
        loadIncomeData();
    }

    private void loadIncomeData() {
        // Fetch income data from Firestore
        firebaseFirestore.collection("Expenses")
                .document(firebaseAuth.getUid())
                .collection("Notes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if documents are found
                        if (task.getResult() != null && task.getResult().size() > 0) {
                            for (DocumentSnapshot ds : task.getResult()) {
                                // Filter for income type and extract data
                                if ("Income".equals(ds.getString("type"))) {
                                    String note = ds.getString("note");
                                    String amountStr = ds.getString("amount");

                                    if (note != null && amountStr != null) {
                                        try {
                                            int amount = Integer.parseInt(amountStr);
                                            // Add entry to data list
                                            dataEntries.add(new ValueDataEntry(note, amount));
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, "Error parsing amount: " + amountStr, e);
                                        }
                                    } else {
                                        Log.e(TAG, "Note or amount is null");
                                    }
                                }
                            }

                            // If data is available, set up the chart
                            if (!dataEntries.isEmpty()) {
                                setupPieChart();
                            } else {

                                Toast.makeText(IncomeChartActivity.this, "No income data found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setupPieChart() {
        // Create and configure the pie chart
        Pie pie = AnyChart.pie();
        pie.data(dataEntries);
        pie.title("Pie Chart for Income Distribution");
        anyChartView.setChart(pie);
    }
}
