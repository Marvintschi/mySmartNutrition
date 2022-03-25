package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class settings extends AppCompatActivity {

    Button btn1;

    Connection connection;
    String ConnectionResult = "";
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn1 = (Button) findViewById(R.id.save_goals);
    }


}