package com.example.mysmartnutrition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class settings extends AppCompatActivity {

    private Button btnSaveGoals;
    private TextView tvUserID;
    private EditText tvChangeKcal, tvChangeSteps, tvChangeWater;

    ProgressDialog progressDialog, progressDialog2;

    private String kcalGoal, stepsGoal, waterGoal;

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

        tvChangeKcal.setText(sharedPreferences.getString(KCAL_GOAL, ""));
        tvChangeSteps.setText(sharedPreferences.getString(STEPS_GOAL, ""));
        tvChangeWater.setText(sharedPreferences.getString(WATER_GOAL, ""));

        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Bitte warten...");


        getDataFromOnlineDB();

        btnSaveGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Speichern der Einstellungen implementieren
                if (!String.valueOf(tvChangeKcal.getText()).equals("")) {
                    kcalGoal = String.valueOf(tvChangeKcal.getText());
                }
                if (!String.valueOf(tvChangeSteps.getText()).equals("")) {
                    stepsGoal = String.valueOf(tvChangeSteps.getText()).trim();
                }
                if (!String.valueOf(tvChangeWater.getText()).equals("")) {
                    waterGoal = String.valueOf(tvChangeWater.getText()).trim();
                }

                if (!String.valueOf(tvChangeKcal.getText()).equals("") && !String.valueOf(tvChangeSteps.getText()).equals("")
                        && !String.valueOf(tvChangeWater.getText()).equals("")) {

                    saveSettings();
                    saveSettingsToDB();
                    finish();

                } else {
                    //Toast.makeText(getApplicationContext(), "Keine Ziele gesetzt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveSettings() {
        kcalGoal = tvChangeKcal.getText().toString().trim();
        stepsGoal = tvChangeSteps.getText().toString().trim();
        waterGoal = tvChangeWater.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KCAL_GOAL, kcalGoal);
        editor.putString(STEPS_GOAL, stepsGoal);
        editor.putString(WATER_GOAL, waterGoal);
        editor.commit();

    }

    public void saveSettingsToDB(){
        progressDialog.setMessage("Übermittle Daten...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATE_GOALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

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
                Map<String, String> params = new HashMap<>();
                String userID = tvUserID.getText().toString().trim();
                params.put("kcal", kcalGoal);
                params.put("step", stepsGoal);
                params.put("water", waterGoal);
                params.put("userID", userID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getDataFromOnlineDB(){

        progressDialog2.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_GOALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog2.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                tvChangeWater.setText(obj.getString("goalWater"));
                                tvChangeSteps.setText(obj.getString("goalSteps"));
                                tvChangeKcal.setText(obj.getString("goalKcal"));
                            }else{
                                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog2.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String userID = tvUserID.getText().toString().trim();
                params.put("userID", userID);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }
}