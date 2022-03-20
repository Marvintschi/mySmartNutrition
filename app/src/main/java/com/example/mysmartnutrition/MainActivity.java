package com.example.mysmartnutrition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Objects from the activity_main.xml file
    private FloatingActionButton addButton;
    private Button addBreakfast;
    private Button addLunch;
    private Button addDinner;
    private TextView tvStepCounter;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isStepCounterPresent;
    private int stepCount = 0;
    private int stepsOfToday = 0;
    public int dayStep = 0;
    public int counterReading = 0;
    /* private List<Integer> savedStepsList = new List<Integer>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<Integer> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return null;
        }

        @Override
        public boolean add(Integer integer) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends Integer> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends Integer> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Integer get(int index) {
            return null;
        }

        @Override
        public Integer set(int index, Integer element) {
            return null;
        }

        @Override
        public void add(int index, Integer element) {

        }

        @Override
        public Integer remove(int index) {
            return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<Integer> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<Integer> listIterator(int index) {
            return null;
        }

        @NonNull
        @Override
        public List<Integer> subList(int fromIndex, int toIndex) {
            return null;
        }
    }; */

    public String savedDate;
    public String notSavedDate;

    public SharedPreferences sharedPreferences = new SharedPreferences() {
        @Override
        public Map<String, ?> getAll() {
            return null;
        }

        @Nullable
        @Override
        public String getString(String key, @Nullable String defValue) {
            return null;
        }

        @Nullable
        @Override
        public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
            return null;
        }

        @Override
        public int getInt(String key, int defValue) {
            return 0;
        }

        @Override
        public long getLong(String key, long defValue) {
            return 0;
        }

        @Override
        public float getFloat(String key, float defValue) {
            return 0;
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return false;
        }

        @Override
        public boolean contains(String key) {
            return false;
        }

        @Override
        public Editor edit() {
            return null;
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }
    };
    public SharedPreferences.Editor editor = new SharedPreferences.Editor() {
        @Override
        public SharedPreferences.Editor putString(String key, @Nullable String value) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            return null;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            return null;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            return null;
        }

        @Override
        public SharedPreferences.Editor clear() {
            return null;
        }

        @Override
        public boolean commit() {
            return false;
        }

        @Override
        public void apply() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        addButton = findViewById(R.id.fab);
        addBreakfast = findViewById(R.id.add_product_breakfast);
        addLunch = findViewById(R.id.add_product_lunch);
        addDinner = findViewById(R.id.add_product_dinner);
        tvStepCounter = findViewById(R.id.stepCounter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });

        addBreakfast.setOnClickListener(new View.OnClickListener() {
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
        });

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isStepCounterPresent = true;
        } else {
            tvStepCounter.setText("Step counter sensor is not present");
            isStepCounterPresent = false;
        }

        savedDate = String.valueOf(java.time.LocalDate.now());

        SQLiteDatabase database = openOrCreateDatabase("mysmartnutrition.db", MODE_PRIVATE, null);
        database.execSQL("create table if not exists test3(date text, dayStep integer, systemCounter integer)");

        /*java.io.File file = new java.io.File("/data/data/com.example.mysmartnutrition/databases/mysmartnutrition.db");
        if (file.exists()) {
        }else
        {
            generateEmptyEntry(savedDate, '0');
        }

        getDataFromDB(savedDate);*/
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, stepCounterSensor);
        }
    }


}