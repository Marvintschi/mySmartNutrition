package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Adapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

public class AddProductDetails extends AppCompatActivity {

    private String barcode = "";
    private String url = "";
    private String produktName, hersteller, fett, energie, kohlenhydrate, proteine, ballastStoffe, menge;

    private TextView tvProduktName, tvHersteller, tvFett, tvEnergie, tvKohlenhydrate, tvProteine, tvBallaststoffe, tvMenge, tvPortionen, tvPortionsgroesse, tvMahlzeitangabe;

    private ProgressDialog progressDialog;

    public void CreateURL() {
        // holt Daten vom Barcode
        Bundle extras = getIntent().getExtras();
            if (extras != null) {
            barcode = extras.getString("barcodeData");
        }

        url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_details);

        tvProduktName = findViewById(R.id.produkt_name);
        tvHersteller = findViewById(R.id.hersteller);
        tvFett = findViewById(R.id.fett);
        tvEnergie = findViewById(R.id.energie);
        tvKohlenhydrate = findViewById(R.id.kohlenhydrate);
        tvProteine = findViewById(R.id.proteine);
        tvBallaststoffe = findViewById(R.id.ballaststoffe);
        tvMenge = findViewById(R.id.menge);
        tvPortionen = findViewById(R.id.portionen);
        tvPortionsgroesse = findViewById(R.id.portionsgroesse);
        tvMahlzeitangabe = findViewById(R.id.mahlzeitangabe);

        new getData().execute();

    }

    class getData extends AsyncTask<String, Void, JSONObject> {

        JSONObject product;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(AddProductDetails.this);
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            URLHandler urlHandler = new URLHandler();
            CreateURL();

            String jsonString = urlHandler.httpServiceCall(url);
            if (jsonString != null) {
                try {

                    JSONObject jsonObject = new JSONObject(jsonString);
                    product = jsonObject.getJSONObject("product");
                    produktName = product.getString("product_name");

                    try {
                        hersteller = product.getString("name");
                    } catch (Exception e) {
                        hersteller = "";
                    }
                    try {
                        fett = String.valueOf(product.getDouble("fat"));
                    } catch (Exception e) {
                        fett = "0";
                    }
                    try {
                        energie = String.valueOf(product.getDouble("energy-kcal"));
                    } catch (Exception e) {
                        energie = "0";
                    }
                    try {
                        kohlenhydrate = String.valueOf(product.getDouble("carbohydrates"));
                    } catch (Exception e) {
                        kohlenhydrate = "0";
                    }
                    try {
                        proteine = String.valueOf(product.getDouble("proteins"));
                    } catch (Exception e) {
                        proteine = "0";
                    }
                    try {
                        ballastStoffe = String.valueOf(product.getDouble("fiber"));
                    } catch (Exception e) {
                        ballastStoffe = "0";
                    }
                    try {
                        menge = product.getString("quantity");
                    } catch (Exception e) {
                        menge = "1";
                    }

                } catch (JSONException e) {

                    finish();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Produkt konnte nicht gefunden werden", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {

                finish();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            tvProduktName.setText(produktName);
            tvHersteller.setText(hersteller);
            tvFett.setText(fett);
            tvEnergie.setText(energie);
            tvKohlenhydrate.setText(kohlenhydrate);
            tvProteine.setText(proteine);
            tvBallaststoffe.setText(ballastStoffe);
            tvMenge.setText(menge);

        }
    }
}