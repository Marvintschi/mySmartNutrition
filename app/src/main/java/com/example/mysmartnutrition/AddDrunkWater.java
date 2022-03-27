package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDrunkWater extends AppCompatActivity {

    private EditText wasserAngabe;
    private Button btnBestätigen;

    private String drunkWater;

    float savedWater, waterToAdd;
    String savedDate = String.valueOf(java.time.LocalDate.now());

    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drunk_water);

        db = new DatabaseHelper(AddDrunkWater.this);

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

    }


}