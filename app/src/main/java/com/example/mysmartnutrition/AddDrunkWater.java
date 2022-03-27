package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDrunkWater extends AppCompatActivity {

    EditText wasserAngabe;
    Button btnBestätigen;

    String drunkWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drunk_water);

        wasserAngabe = findViewById(R.id.editText_drunk_water);

        btnBestätigen = findViewById(R.id.bestätigen);

        btnBestätigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drunkWater = wasserAngabe.getText().toString();
                if(drunkWater.equals("")){
                    Toast.makeText(getApplicationContext(), "Bitte das getrunkene Wasser eingeben", Toast.LENGTH_SHORT).show();
                }
                else {


                    // better than using an intent
                    finish();
                }
            }
        });
    }
}