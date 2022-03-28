package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class CalenderDateSelect extends AppCompatActivity {

    private CalendarView calendarView;
    private Button bestätigen;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_date_select);

        calendarView = findViewById(R.id.calendarView);

        bestätigen = findViewById(R.id.datum_bestätigen);

        bestätigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = String.valueOf(calendarView.getDate());

                // TODO Fertigstellen

                finish();
            }
        });
    }
}