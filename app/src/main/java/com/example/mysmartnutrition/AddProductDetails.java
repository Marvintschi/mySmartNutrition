package com.example.mysmartnutrition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.FirstPartyScopes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductDetails extends AppCompatActivity {

    private String barcode = "";
    private String url = "";
    private String produktName, hersteller, fett, energie, kohlenhydrate, proteine, ballastStoffe, menge;

    private TextView tvProduktName, tvHersteller, tvFett, tvEnergie, tvKohlenhydrate, tvProteine, tvBallaststoffe, tvMenge, tvPortionen, tvPortionsgroesse, tvMahlzeitangabe;
    private EditText amountConsumed;
    private Spinner spinner;
    private ProgressDialog progressDialog;

    String savedDate = String.valueOf(java.time.LocalDate.now());
    String consumed;
    String UserID;

    ProgressDialog progressDialog2;


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

        amountConsumed = (EditText) findViewById(R.id.editTextNumber);

        final List<String> items = Arrays.asList("Frühstück", "Mittagessen", "Abendessen", "Snack");

        spinner = (Spinner) findViewById(R.id.mahlzeitangabe);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.my_selected_item, items);

        adapter.setDropDownViewResource(R.layout.my_dropdown_item);

        spinner.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        UserID =sharedPreferences.getString(MainActivity.USER_ID, "Error");


        new getData().execute();

        progressDialog2 = new ProgressDialog(this);

    }



    class getData extends AsyncTask<String, Void, JSONObject> {

        JSONObject product, nutriments;


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
                    //product = product.getJSONObject("product");
                    product = jsonObject.getJSONObject("product");
                    produktName = product.getString("product_name");

                    try {
                        //product = jsonObject.getJSONObject("product");
                        /*int a  = 0;
                        sources = new JSONArray(product.getJSONObject("sources"));
                        System.out.print(sources);
                        hersteller = sources.getJSONObject(0).toString();*/
                        hersteller = product.getString("owner");
                        hersteller = hersteller.replace("org-", "");
                        hersteller = hersteller.replace("-", " ");

                    } catch (Exception e) {
                        hersteller = "";
                    }
                    try {
                        nutriments = product.getJSONObject("nutriments");
                        fett = String.valueOf(nutriments.getDouble("fat_100g"));

                    } catch (Exception e) {
                        fett = "0";

                    }
                    try {
                        nutriments = product.getJSONObject("nutriments");
                        energie = String.valueOf(nutriments.getString("energy-kcal"));
                    } catch (Exception e) {
                        energie = "0";
                    }
                    try {
                        nutriments = product.getJSONObject("nutriments");
                        kohlenhydrate = String.valueOf(nutriments.getString("carbohydrates"));
                    } catch (Exception e) {
                        kohlenhydrate = "0";
                    }
                    try {
                        nutriments = product.getJSONObject("nutriments");
                        proteine = String.valueOf(nutriments.getDouble("proteins"));
                    } catch (Exception e) {
                        proteine = "0";
                    }
                    try {
                        nutriments = product.getJSONObject("nutriments");
                        ballastStoffe = String.valueOf(nutriments.getDouble("fiber"));
                    } catch (Exception e) {
                        ballastStoffe = "0";
                    }
                    try {
                        menge = product.getString("quantity");
                        menge = menge.replace(" e", "");
                        menge = menge.replace("e ", "");
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

    public void pushData(View view){
        DatabaseHelper db;
        db = new DatabaseHelper(AddProductDetails.this);
        consumed = amountConsumed.getText().toString();
        if(consumed == ""){
            Toast.makeText(getApplicationContext(), "Bitte die Menge eingeben", Toast.LENGTH_SHORT).show();
        }
        else {
            String meal = spinner.getSelectedItem().toString();
            db.insertDataToDB(savedDate, produktName, hersteller, barcode, energie, kohlenhydrate, fett, proteine, ballastStoffe, consumed, meal);
            Intent intent = new Intent(AddProductDetails.this, MainActivity.class);
            startActivity(intent);
        }

        saveDataToOnlineDB();

    }

    public void saveDataToOnlineDB(){
        String [] dateArray = savedDate.split("-");
        String savedDateFormatted = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];
        progressDialog2.setMessage("Übermittle Daten...");
        progressDialog2.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_GENERATE_FOOD_ENTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog2.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                consumed = amountConsumed.getText().toString();
                String meal = spinner.getSelectedItem().toString();

                params.put("userID", UserID);
                params.put("productName", produktName);
                params.put("productManufacture", hersteller);
                params.put("eanNumber", barcode);
                params.put("creationDate", savedDateFormatted); //savedDate with changed Format "YYYY-MM-DD" went to "DD.MM.YYYY"
                params.put("kcal", energie);
                params.put("carbohydrates", kohlenhydrate);
                params.put("fat", fett);
                params.put("protein", proteine);
                params.put("fiber", ballastStoffe);
                params.put("amountConsumed", consumed);
                params.put("meal", meal);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}