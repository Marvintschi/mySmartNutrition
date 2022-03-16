package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AddProductDetails extends AppCompatActivity {

    private TextView tvPortionen, tvPortionsgroesse, tvMahlzeitangabe;

    private String barcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_details);

        tvPortionen = findViewById(R.id.portionen);
        tvPortionsgroesse = findViewById(R.id.portionsgroesse);
        tvMahlzeitangabe = findViewById(R.id.mahlzeitangabe);

        // holt Daten vom Barcode
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barcode = extras.getString("barcodeData");
        }

        String URL = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";








        // hier beim nächsten mal weitermachen!!!
        tvPortionen.setText(URL);
        // !!!
    }

}