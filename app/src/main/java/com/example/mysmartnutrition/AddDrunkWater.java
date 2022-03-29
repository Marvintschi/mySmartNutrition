package com.example.mysmartnutrition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddDrunkWater extends AppCompatActivity {

    private EditText wasserAngabe;
    private Button btnBestätigen;

    private String drunkWater;

    float savedWater, waterToAdd;
    String savedDate = String.valueOf(java.time.LocalDate.now());

    DatabaseHelper db;

    ProgressDialog progressDialog2;
    String UserID2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drunk_water);

        db = new DatabaseHelper(AddDrunkWater.this);

        progressDialog2 = new ProgressDialog(this);



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
                    // TODO Hier noch die einträge in die Datenbank durchführen
                    if(checkIsEntryAlreadyInDB(savedDate) == false){
                        generateEmptyEntry(savedDate, 0);
                    }
                    getDataFromDB(savedDate);

                    waterToAdd = Float.valueOf(wasserAngabe.getText().toString());
                    savedWater = savedWater + waterToAdd;

                    pushWaterToDB(savedWater, savedDate);

                    pushWaterToOnlineDB();

                    Intent intent = new Intent(AddDrunkWater.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void pushWaterToDB(float water, String date){
        SQLiteDatabase database = openOrCreateDatabase("water.db", MODE_PRIVATE, null);
        database.execSQL("UPDATE water SET waterDay = "+"'"+ water +"' WHERE date = " + "'" + date + "'");
        database.close();
    }

    public void getDataFromDB(String date){
        SQLiteDatabase database = openOrCreateDatabase("water.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from water where date = '" + date + "'", null);

        cursor.moveToFirst();

        savedWater = Float.valueOf(cursor.getString(1));
        cursor.close();
        database.close();
    }

    public boolean checkIsEntryAlreadyInDB(String date){
        SQLiteDatabase database = openOrCreateDatabase("water.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from water where date = '" + date + "'", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            database.close();
            return false;
        }
        cursor.close();
        database.close();
        return true;
    }

    public void generateEmptyEntry(String date, float water){
        SQLiteDatabase database = openOrCreateDatabase("water.db", MODE_PRIVATE, null);
        database.execSQL("create table if not exists water(date text, waterDay float)");
        database.execSQL("insert into water values('" + date + "' , '" + water + "')");
        database.close();
    }

    public void pushWaterToOnlineDB(){
        String [] dateArray = savedDate.split("-");
        String savedDateFormatted = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];
        progressDialog2.setMessage("Übermittle Daten...");
        //progressDialog2.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATE_WATER,
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
                        //Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                UserID2 =sharedPreferences.getString(MainActivity.USER_ID, "Error");

                Map<String, String> params = new HashMap<>();
                params.put("userID", UserID2);
                params.put("water", String.valueOf(savedWater));
                params.put("date", savedDateFormatted);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}