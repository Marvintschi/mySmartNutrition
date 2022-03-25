package com.example.mysmartnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class settings extends AppCompatActivity {

    Button btn1;

    Connection connection;
    String ConnectionResult = "";
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn1 = (Button) findViewById(R.id.save_goals);
    }

    public void GetTextFromSQL(View view){
        try{
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionclass();
            if(connection!=null){
                String query = "SELECT * FROM User WHERE User_ID = '12345678'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()){
                    a = resultSet.getString(3);
                    Toast.makeText(getApplicationContext(), a + " result", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                ConnectionResult = "Check Connection";
            }
        }catch (Exception e){

        }

    }

}