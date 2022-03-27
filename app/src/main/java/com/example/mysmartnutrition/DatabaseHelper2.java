package com.example.mysmartnutrition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper2 extends SQLiteOpenHelper {

    private static final String DB_NAME = "water.db";
    private static final String DB_TABLE = "water";

    //colums
    private static final String DATE = "date";
    private static final String water = "waterDay";

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (" +
            DATE + " TEXT, " +
            water + " FLOAT " + ")";

    public DatabaseHelper2(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        onCreate(db);
    }

    /*public void insertDataToDB(String date, String product_name, String manufacture, String eanCode, String kcal, String carbohydrate, String fat, String protein, String fiber, String consumed, String meal){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + DB_TABLE + " (DATE, PRODUCT_NAME, PRODUCT_MANUFACTURE, EAN_CODE, kcalPER100, carbohydratePER100, fatPER100, proteinPER100, fiberPER100, amountConsumed, meal) " +
                "VALUES('" + date + "', '" + product_name + "', '" + manufacture + "', '" + eanCode + "', '" + kcal + "', '" + carbohydrate + "', '" + fat + "', '" + protein + "', '" + fiber + "', '" + consumed + "', '" + meal + "')");

    }*/


    //create method to view Data
    Cursor viewData(String date){
        String query = "SELECT * FROM " + DB_TABLE + " WHERE date = '" + date + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}