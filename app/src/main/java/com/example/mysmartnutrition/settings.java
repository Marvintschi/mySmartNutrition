package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class settings extends AppCompatActivity {

    Button btnSaveGoals;

    TextView tvUserID;
    EditText tvChangeKcal, tvChangeSteps, tvChangeWater;

    Connection connection;
    String ConnectionResult = "";
    String kcalGoal, stepsGoal, waterGoal;

    static final String KCAL_GOAL = "kcalGoal";
    static final String STEPS_GOAL = "stepsGoal";
    static final String WATER_GOAL = "waterGoal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvUserID = findViewById(R.id.user_id_tv);
        tvChangeKcal = findViewById(R.id.editTextKcal);
        tvChangeSteps = findViewById(R.id.editTextSteps);
        tvChangeWater = findViewById(R.id.editTextWater);
        btnSaveGoals = findViewById(R.id.save_goals);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        tvUserID.setText(sharedPreferences.getString(MainActivity.USER_ID, "Error"));

        btnSaveGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Speichern der Einstellungen implementieren
                if (!String.valueOf(tvChangeKcal.getText()).equals("")) {
                    kcalGoal = String.valueOf(tvChangeKcal.getText());
                }
                if (!String.valueOf(tvChangeSteps.getText()).equals("")) {
                    stepsGoal = String.valueOf(tvChangeSteps.getText());
                }
                if (!String.valueOf(tvChangeWater.getText()).equals("")) {
                    waterGoal = String.valueOf(tvChangeWater.getText());
                }

                if (!String.valueOf(tvChangeKcal.getText()).equals("") && !String.valueOf(tvChangeSteps.getText()).equals("")
                        && !String.valueOf(tvChangeWater.getText()).equals("")) {

                    saveSettings();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Keine Ziele gesetzt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KCAL_GOAL, kcalGoal);
        editor.putString(STEPS_GOAL, stepsGoal);
        editor.putString(WATER_GOAL, waterGoal);
        editor.commit();
    }


}