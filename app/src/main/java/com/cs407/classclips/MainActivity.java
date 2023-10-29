package com.cs407.classclips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //go to welcome activity
    public void onClickLogin(View view){
        EditText username = (EditText) findViewById(R.id.username);
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.lab5_milestone", MODE_PRIVATE);
        sharedPreferences.edit().putString("username", username.getText().toString()).apply();
        goToActivity(username.getText().toString());
    }

    public void goToActivity(String s){
        Intent intent=new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}