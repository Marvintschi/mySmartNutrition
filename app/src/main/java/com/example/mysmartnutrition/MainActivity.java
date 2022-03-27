package com.example.mysmartnutrition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Objects from the activity_main.xml file
    private FloatingActionButton addButton;
    private Button addBreakfast;
    private Button addLunch;
    private Button addDinner;
    private TextView tvStepCounter , totalBreakfast, totalLunch, totalDinner, totalSnack, aufgebrauchtKcal, tvTageswertLimit, tvWasserZiel, tvStepsKcal;
    private AnyChartView nutritionChart;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isStepCounterPresent;
    private int stepCount = 0;
    private int stepsOfToday = 0;
    public int dayStep = 0;
    public int counterReading = 0;

    public String savedDate;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userID";

    static Random random = new Random();
    public final static int UserID = random.nextInt(10000000);

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DatabaseHelper db;
    DatabaseHelper2 db2;
    ProgressDialog progressDialog, progressDialog2;
    String UserID2;


    RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerView4;

    ArrayList<String> product_name_breakfast = null, product_manufacture_breakfast, product_kcal_breakfast;
    ArrayList<String> product_name_lunch = null, product_manufacture_lunch, product_kcal_lunch;
    ArrayList<String> product_name_dinner = null, product_manufacture_dinner, product_kcal_dinner;
    ArrayList<String> product_name_snacks = null, product_manufacture_snacks, product_kcal_snacks;

    CustomAdapter customAdapter, Adapter, Adapter2, Adapter3;

    //kcal results
    int result1, result2, result3, result4;
    //nährwerte results
    float resultCarb, resultFiber, resultFat, resultProtein;
    //wasser result
    float resultWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        totalBreakfast = findViewById(R.id.gesamtwert_breakfast);
        totalLunch = findViewById(R.id.gesamtwert_lunch);
        totalDinner = findViewById(R.id.gesamtwert_dinner);
        totalSnack = findViewById(R.id.gesamtwert_snacks);

        aufgebrauchtKcal = findViewById(R.id.tageswert_aufgebraucht);
        tvTageswertLimit = findViewById(R.id.tageswert_limit);
        tvWasserZiel = findViewById(R.id.wasser_tagesziel);
        tvStepsKcal = findViewById(R.id.stepKcal);

        addButton = findViewById(R.id.fab);
        addBreakfast = findViewById(R.id.add_product_breakfast);
        addLunch = findViewById(R.id.add_product_lunch);
        addDinner = findViewById(R.id.add_product_dinner);
        tvStepCounter = findViewById(R.id.stepCounter);
        nutritionChart = findViewById(R.id.nutrition_chart_view);

        recyclerView = findViewById(R.id.lvContact);
        recyclerView2 = findViewById(R.id.lvContact2);
        recyclerView3 = findViewById(R.id.lvContact3);
        recyclerView4 = findViewById(R.id.lvContact4);

        savedDate = String.valueOf(java.time.LocalDate.now());

        db = new DatabaseHelper(MainActivity.this);
        db2 = new DatabaseHelper2(MainActivity.this);
        //db.insertDataToDB();
        product_name_breakfast = new ArrayList<>();
        product_manufacture_breakfast = new ArrayList<>();
        product_kcal_breakfast = new ArrayList<>();

        product_name_lunch = new ArrayList<>();
        product_manufacture_lunch = new ArrayList<>();
        product_kcal_lunch = new ArrayList<>();

        product_name_dinner = new ArrayList<>();
        product_manufacture_dinner = new ArrayList<>();
        product_kcal_dinner = new ArrayList<>();

        product_name_snacks = new ArrayList<>();
        product_manufacture_snacks = new ArrayList<>();
        product_kcal_snacks = new ArrayList<>();


        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString(USER_ID, null) != null) {
            // hier soll nichts getan werden
        } else {
            checkIfUserIDisAllreadyInUse();
            editor.putString(USER_ID, String.valueOf(UserID));
            editor.commit();
        }

        tvTageswertLimit.setText(sharedPreferences.getString(settings.KCAL_GOAL, "1000") + " kcal");
        tvWasserZiel.setText(sharedPreferences.getString(settings.WATER_GOAL, "2") + " L");

        resultCarb = 0;
        resultProtein = 0;
        resultFat = 0;
        resultFiber = 0;

        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        database.execSQL("create table if not exists test3(date text, dayStep integer, systemCounter integer)");
        SQLiteDatabase database2 = openOrCreateDatabase("water.db", MODE_PRIVATE, null);
        database2.execSQL("create table if not exists water(date text, waterDay float)");

        storeDataInArrays("Frühstück", savedDate);
        customAdapter = new CustomAdapter(MainActivity.this, product_name_breakfast, product_manufacture_breakfast, product_kcal_breakfast);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        storeDataInArrays1("Mittagessen", savedDate);
        Adapter = new CustomAdapter(MainActivity.this, product_name_lunch, product_manufacture_lunch, product_kcal_lunch);
        recyclerView2.setAdapter(Adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        storeDataInArrays2("Abendessen", savedDate);
        Adapter2 = new CustomAdapter(MainActivity.this, product_name_dinner, product_manufacture_dinner, product_kcal_dinner);
        recyclerView3.setAdapter(Adapter2);
        recyclerView3.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        storeDataInArrays3("Snack", savedDate);
        Adapter3 = new CustomAdapter(MainActivity.this, product_name_snacks, product_manufacture_snacks, product_kcal_snacks);
        recyclerView4.setAdapter(Adapter3);
        recyclerView4.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //get and show the water
        try {
            storeWater(savedDate);
        }
        catch (Exception e){

        }

        int currentKcal = result1 + result2 + result3 + result4;
        aufgebrauchtKcal.setText(String.valueOf(currentKcal) + " kcal" + "");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });



        /*addBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        addLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        addDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });*/

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isStepCounterPresent = true;
        } else {
            tvStepCounter.setText("Step counter sensor is not present");
            isStepCounterPresent = false;
        }

        setupNutritionChart();



        /*java.io.File file = new java.io.File("/data/data/com.example.mysmartnutrition/databases/mysmartnutrition.db");
        if (file.exists()) {
        }else
        {
            generateEmptyEntry(savedDate, '0');
        }

        getDataFromDB(savedDate);*/
    }

    public void showSettings(View view){
        Intent intent = new Intent(MainActivity.this, settings.class);
        startActivity(intent);
    }

    public void pushStepsToDB(int dayStep, int stepCount, String date){
        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        //database.execSQL("create table if not exists test3(date text, dayStep integer, systemCounter integer)");
        database.execSQL("UPDATE test3 SET dayStep = "+"'"+ dayStep +"', systemCounter = '" + stepCount + "' WHERE date = " + "'" + date + "'");
        database.close();
    }

    public void getDataFromDB(String date){
        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from test3 where date = '" + date + "'", null);

        cursor.moveToFirst();

        dayStep = Integer.valueOf(cursor.getString(1));
        counterReading = Integer.valueOf(cursor.getString(2));
        cursor.close();
        database.close();
    }

    public boolean checkIsEntryAlreadyInDB(String date){
        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from test3 where date = '" + date + "'", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            database.close();
            return false;
        }
        cursor.close();
        database.close();
        return true;
    }

    public void generateEmptyEntry(String date, int counter){
        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        database.execSQL("create table if not exists test3(date text, dayStep integer, systemCounter integer)");
        database.execSQL("insert into test3 values('" + savedDate + "' , '0' , '" + stepCount + "')");
        database.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == stepCounterSensor) {
            stepCount = (int) event.values[0];
            savedDate = String.valueOf(java.time.LocalDate.now());
            if(checkIsEntryAlreadyInDB(savedDate) == false){
                generateEmptyEntry(savedDate, '0');
            }
            getDataFromDB(savedDate);

            dayStep = dayStep + (stepCount - counterReading);

            pushStepsToDB(dayStep, stepCount, savedDate);

            tvStepCounter.setText(String.valueOf(dayStep));

            float stepsInKcal = dayStep * 0.035f;
            tvStepsKcal.setText("ca." + String.format("%.3f", stepsInKcal) + " kcal");

            pushStepsToOnlineDB();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
        tvTageswertLimit.setText(sharedPreferences.getString(settings.KCAL_GOAL, "2500") + " kcal");
        tvWasserZiel.setText(sharedPreferences.getString(settings.WATER_GOAL, "2") + " L");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, stepCounterSensor);
        }
    }

    public void storeDataInArrays(String meal, String date){
        Cursor cursor = db.viewData(meal, date);
        result1 = 0;


        if(cursor.getCount() == 0){
            // Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                product_name_breakfast.add(cursor.getString(1));
                product_manufacture_breakfast.add(cursor.getString(2));
                float kcal = Float.valueOf(cursor.getString(4));
                float amountConsumed = Float.valueOf(cursor.getString(9));
                float kcalTotal = Math.round(amountConsumed * (kcal/100));


                resultCarb = resultCarb + (((Float.valueOf(cursor.getString(5)))/100) * amountConsumed);
                resultFat = resultFat + (((Float.valueOf(cursor.getString(6)))/100) * amountConsumed);
                resultProtein = resultProtein + (((Float.valueOf(cursor.getString(7)))/100) * amountConsumed);
                resultFiber = resultFiber + (((Float.valueOf(cursor.getString(8)))/100) * amountConsumed);


                result1 = result1 + (int) kcalTotal;
                int kcalSingle = (int) kcalTotal;
                product_kcal_breakfast.add(String.valueOf(kcalSingle));
            }
            totalBreakfast.setText(String.valueOf(result1) + " kcal");
        }
    }

    public void storeDataInArrays1(String meal, String date){
        Cursor cursor = db.viewData(meal, date);
        result2 = 0;

        if(cursor.getCount() == 0){
            // Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                product_name_lunch.add(cursor.getString(1));
                product_manufacture_lunch.add(cursor.getString(2));
                float kcal = Float.valueOf(cursor.getString(4));
                float amountConsumed = Float.valueOf(cursor.getString(9));
                float kcalTotal = Math.round(amountConsumed * (kcal/100));
                result2 = result2 + (int) kcalTotal;

                resultCarb = resultCarb + (((Float.valueOf(cursor.getString(5)))/100) * amountConsumed);
                resultFat = resultFat + (((Float.valueOf(cursor.getString(6)))/100) * amountConsumed);
                resultProtein = resultProtein + (((Float.valueOf(cursor.getString(7)))/100) * amountConsumed);
                resultFiber = resultFiber + (((Float.valueOf(cursor.getString(8)))/100) * amountConsumed);

                int kcalSingle = (int) kcalTotal;
                product_kcal_lunch.add(String.valueOf(kcalSingle));
            }
            totalLunch.setText(String.valueOf(result2) + " kcal");
        }
    }

    public void storeDataInArrays2(String meal, String date){
        Cursor cursor = db.viewData(meal, date);
        result3 = 0;

        if(cursor.getCount() == 0){
            // Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                product_name_dinner.add(cursor.getString(1));
                product_manufacture_dinner.add(cursor.getString(2));
                float kcal = Float.valueOf(cursor.getString(4));
                float amountConsumed = Float.valueOf(cursor.getString(9));
                float kcalTotal = Math.round(amountConsumed * (kcal/100));
                result3 = result3 + (int) kcalTotal;

                resultCarb = resultCarb + (((Float.valueOf(cursor.getString(5)))/100) * amountConsumed);
                resultFat = resultFat + (((Float.valueOf(cursor.getString(6)))/100) * amountConsumed);
                resultProtein = resultProtein + (((Float.valueOf(cursor.getString(7)))/100) * amountConsumed);
                resultFiber = resultFiber + (((Float.valueOf(cursor.getString(8)))/100) * amountConsumed);

                int kcalSingle = (int) kcalTotal;
                product_kcal_dinner.add(String.valueOf(kcalSingle));
            }
            totalDinner.setText(String.valueOf(result3) + " kcal");
        }
    }

    public void storeDataInArrays3(String meal, String date){
        Cursor cursor = db.viewData(meal, date);
        result4 = 0;

        if(cursor.getCount() == 0){
            // Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                product_name_snacks.add(cursor.getString(1));
                product_manufacture_snacks.add(cursor.getString(2));
                float kcal = Float.valueOf(cursor.getString(4));
                float amountConsumed = Float.valueOf(cursor.getString(9));
                float kcalTotal = Math.round(amountConsumed * (kcal/100));
                result4 = result4 + (int) kcalTotal;

                resultCarb = resultCarb + (((Float.valueOf(cursor.getString(5)))/100) * amountConsumed);
                resultFat = resultFat + (((Float.valueOf(cursor.getString(6)))/100) * amountConsumed);
                resultProtein = resultProtein + (((Float.valueOf(cursor.getString(7)))/100) * amountConsumed);
                resultFiber = resultFiber + (((Float.valueOf(cursor.getString(8)))/100) * amountConsumed);

                int kcalSingle = (int) kcalTotal;
                product_kcal_snacks.add(String.valueOf(kcalSingle));
            }
            totalSnack.setText(String.valueOf(result4) + " kcal");
        }
    }

    public void storeWater(String date){
        Cursor cursor2 = db2.viewData(date);


        if(cursor2.getCount() == 0){
            // Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor2.moveToNext()){
                float water = Float.valueOf(cursor2.getFloat(1));
                resultWater = water;

            }
            tvWasserZiel.setText(resultWater + " Liter");
        }
    }

    public void setupNutritionChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        String[] beispielWerte = {" Kohlenhydrate", " Ballaststoffe", " Fett", " Eiweiß"};
        int[] beispielZahlen = {Math.round(resultCarb), Math.round(resultFiber), Math.round(resultFat), Math.round(resultProtein)};

        for (int i = 0; i < beispielWerte.length; i++) {
            dataEntries.add(new ValueDataEntry(beispielWerte[i], beispielZahlen[i]));
        }

        pie.data(dataEntries);
        pie.title("Nährwerte des Tages");
        pie.background().fill("#010A43");
        nutritionChart.setChart(pie);
    }

    public void checkIfUserIDisAllreadyInUse(){

        progressDialog.setMessage("Übermittle Daten...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_GENERATE_USER,
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
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", String.valueOf(UserID));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void pushStepsToOnlineDB(){
        String [] dateArray = savedDate.split("-");
        String savedDateFormatted = dateArray[2] + "." + dateArray[1] + "." + dateArray[0];
        progressDialog2.setMessage("Übermittle Daten...");
        //progressDialog2.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATE_STEPS,
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
                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                UserID2 =sharedPreferences.getString(MainActivity.USER_ID, "Error");

                Map<String, String> params = new HashMap<>();
                params.put("userID", UserID2);
                params.put("countedSteps", String.valueOf(dayStep));
                params.put("date", savedDateFormatted);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}