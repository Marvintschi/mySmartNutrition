package com.example.mysmartnutrition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String monthS = String.valueOf(month);
                String yearS = String.valueOf(year);
                String dayS = String.valueOf(dayOfMonth);

                if(monthS.length() < 2){
                    monthS = "0" + monthS;
                }
                if(dayS.length() < 2){
                    dayS = "0" + dayS;
                }

                selectedDate = yearS + "-" + monthS + "-" + dayS;
            }
        });

        bestätigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CalenderDateSelect.this, MainActivity.class);
                intent.putExtra("date", selectedDate);
                startActivity(intent);
                finish();
            }
        });
    }
}