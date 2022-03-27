package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddProduct extends AppCompatActivity {

    Button barcodeButton, btnWasserAngabe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        barcodeButton = findViewById(R.id.barcode_button);
        btnWasserAngabe = findViewById(R.id.wasser_angeben);

        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProduct.this, BarcodeScanner.class);
                startActivity(intent);
            }
        });

        btnWasserAngabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProduct.this, AddDrunkWater.class);
                startActivity(intent);
            }
        });
    }
}