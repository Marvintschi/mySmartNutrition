package com.example.mysmartnutrition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "User.db";
    private static final String DB_TABLE = "User_Table2";

    //colums
    private static final String DATE = "DATE";
    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final String PRODUCT_MANUFACTURE = "PRODUCT_MANUFACTURE";
    private static final String EAN_CODE = "EAN_CODE";
    private static final String kcalPER100 = "kcalPER100";
    private static final String carbohydratePER100 = "carbohydratePER100";
    private static final String fatPER100 = "fatPER100";
    private static final String proteinPER100 = "proteinPER100";
    private static final String fiberPER100 = "fiberPER100";
    private static final String amountConsumed = "amountConsumed";
    private static final String meal = "meal";

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (" +
            DATE + " TEXT, " +
            PRODUCT_NAME + " TEXT, " +
            PRODUCT_MANUFACTURE + " TEXT, " +
            EAN_CODE + " INTEGER, " +
            kcalPER100 + " DECIMAL, " +
            carbohydratePER100 + " TEXT, " +
            fatPER100 + " TEXT, " +
            proteinPER100 + " TEXT, " +
            fiberPER100 + " TEXT, " +
            amountConsumed + " TEXT, " +
            meal + " TEXT " + ")";

    public DatabaseHelper(Context context){
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

    public void insertDataToDB(String date, String product_name, String manufacture, String eanCode, String kcal, String carbohydrate, String fat, String protein, String fiber, String consumed, String meal){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + DB_TABLE + " (DATE, PRODUCT_NAME, PRODUCT_MANUFACTURE, EAN_CODE, kcalPER100, carbohydratePER100, fatPER100, proteinPER100, fiberPER100, amountConsumed, meal) " +
                "VALUES('" + date + "', '" + product_name + "', '" + manufacture + "', '" + eanCode + "', '" + kcal + "', '" + carbohydrate + "', '" + fat + "', '" + protein + "', '" + fiber + "', '" + consumed + "', '" + meal + "')");

    }


    //create method to view Data
    Cursor viewData(String meal, String date){
        String query = "SELECT * FROM " + DB_TABLE + " WHERE meal = '" + meal + "' AND DATE = '" + date + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}