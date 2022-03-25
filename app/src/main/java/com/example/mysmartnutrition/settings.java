package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class settings extends AppCompatActivity {

    Button btn1;

    TextView tvUserID;

    Connection connection;
    String ConnectionResult = "";
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvUserID = findViewById(R.id.user_id_tv);
        btn1 = findViewById(R.id.save_goals);


        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        tvUserID.setText(sharedPreferences.getString(MainActivity.USER_ID, "Error"));
    }


}