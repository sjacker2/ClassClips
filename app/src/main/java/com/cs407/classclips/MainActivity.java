package com.cs407.classclips;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public Map<String, String> userCredentials = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        // = new HashMap<>();

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

                        if (userCredentials.containsKey(newUsername)) {
                            Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            userCredentials.put(newUsername, newPassword);
                            Toast.makeText(MainActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
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

    }

    //go to welcome activity
    public void onClickLogin(View view){
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String enteredUsername = username.getText().toString();
        String enteredPassword = password.getText().toString();

        if(userCredentials.containsKey(enteredUsername)) {
            if(userCredentials.get(enteredUsername).equals(enteredPassword)) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", enteredUsername);
                editor.putString("password", enteredPassword);
                editor.apply();
                goToActivity(enteredUsername);
            }
            else {
                //Password is incorrect
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
        }
        /*
        (SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.classclips", MODE_PRIVATE);
        sharedPreferences.edit().putString("username", username.getText().toString()).apply();
        goToActivity(username.getText().toString());

         */
    }
/*
    private void onClickSignUp(View view) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String newUsername = username.getText().toString();
        String newPassword = password.getText().toString();

        if(userCredentials.containsKey(newUsername)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
        else {
            userCredentials.put(newUsername, newPassword);
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
        }
    }


 */
    public void goToActivity(String s){
        Intent intent=new Intent(this, WelcomeActivity.class);
        //intent.putExtra("username", username);
        startActivity(intent);
    }
}