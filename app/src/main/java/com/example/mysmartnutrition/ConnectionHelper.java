package com.example.mysmartnutrition;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;

    String username, password, ip, port, database;

    @SuppressLint("NewApi")
    public Connection connectionclass(){
        ip = "db5006456980.hosting-data.io";
        database = "dbs5372337";
        username = "dbu3086662";
        password = "dsg23@dsg1!rr?3fh8";
        port = "3306";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port+ ";" + "databasename="+ database+";user=" + username + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }catch (Exception e){
            Log.e("Error ", e.getMessage());
        }
        return connection;
    }
}
