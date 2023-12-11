package com.cs407.classclips;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

//import kotlin._Assertions;

public class MainActivity extends AppCompatActivity {
    //public Map<String, String> userCredentials = new HashMap<>();

    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);

        // check if a user was previously logged in
        String lastUser = sharedPreferences.getString("username", "");
        if (lastUser != "") {
            goToActivity(lastUser);
        } else {
            setContentView(R.layout.activity_main);
        }

        getSupportActionBar().hide();

        // = new HashMap<>();

        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("users", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase, getApplicationContext());

        dbHelper.userExists("adam");

        Button signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sign Up");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dislog_signup, null);
                builder.setView(dialogView);

                EditText username = dialogView.findViewById(R.id.dialog_username);
                EditText password = dialogView.findViewById(R.id.dialog_password);

                builder.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = username.getText().toString();
                        String newPassword = password.getText().toString();

                        if (dbHelper.userExists(newUsername)) {
                            Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //userCredentials.put(newUsername, newPassword);
                            dbHelper.addUser(newUsername, newPassword);
                            sharedPreferences.edit().putString("username", newUsername).apply();
                            Toast.makeText(MainActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            goToActivity(newUsername); // login once a user signs up
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));

        Button loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                if(dbHelper.userExists(enteredUsername)) {
                    if(dbHelper.checkUserPass(enteredUsername, enteredPassword)) {
                        sharedPreferences.edit().putString("username", enteredUsername).apply();
                        goToActivity(enteredUsername);
                    }
                    else {
                        //Password is incorrect
                        Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }
        }));

    }

    public void goToActivity(String s){
        Intent intent=new Intent(this, WelcomeActivity.class);
        //intent.putExtra("username", username);
        startActivity(intent);
    }
}