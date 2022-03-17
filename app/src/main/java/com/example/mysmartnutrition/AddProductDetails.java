package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

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
    private String produktName, fett, energie, zucker, kohlenhydrate, proteine, menge;

    private TextView tvPortionen, tvPortionsgroesse, tvMahlzeitangabe;


    private static String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int count;
        while ((count = reader.read()) != -1) {
            stringBuilder.append((char) count);
        }
        return stringBuilder.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream inputStream = new URL(url).openStream(); // --> hier crasht die App --> Caused by: android.os.NetworkOnMainThreadException
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            inputStream.close();
        }
    }

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

        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        try {
            JSONObject jsonObject = readJsonFromUrl(url);
            JSONObject product = jsonObject.getJSONObject("product");
            /* JSONObject produktName_json = jsonObject.getJSONObject("product_name");
            JSONObject fett_json = jsonObject.getJSONObject("fat");
            JSONObject energie_json = jsonObject.getJSONObject("energy");
            JSONObject zucker_json = jsonObject.getJSONObject("sugars");
            JSONObject kohlenhydrate_json = jsonObject.getJSONObject("carbohydrates");
            JSONObject proteine_json = jsonObject.getJSONObject("proteins");
            JSONObject menge_json = jsonObject.getJSONObject("quantity"); */

            produktName = product.getString("product_name");
            fett = product.getString("fat");
            energie = product.getString("energy");

            menge = product.getString("quantity");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvPortionsgroesse.setText(menge);
        tvPortionen.setText("energie:" + energie);
    }
}