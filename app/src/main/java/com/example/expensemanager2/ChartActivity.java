package com.example.expensemanager2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);

        int income = getIntent().getIntExtra("income", 0);
        int expense = getIntent().getIntExtra("expense", 0);

        if (income == 0 && expense == 0) {
            Toast.makeText(this, "No data available for Income and Expense", Toast.LENGTH_SHORT).show();
        } else {
            com.anychart.charts.Pie pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();
            data.add(new ValueDataEntry("Income", income));
            data.add(new ValueDataEntry("Expense", expense));

            pie.data(data);
            pie.title("Income vs Expense");

            anyChartView.setChart(pie);
        }
    }
}
