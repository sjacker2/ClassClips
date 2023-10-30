package com.cs407.classclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.nav_back){
                //navigate to previous screen
                finish();
                return true;
            }else if(itemId == R.id.nav_home){ //navigate to welcome activity
                Intent intent2 = new Intent(this, WelcomeActivity.class);
                startActivity(intent2);
                return true;
            }else if(itemId == R.id.nav_help){
                //go to help screen
                Intent intent2 = new Intent(this, help_activity.class);
                startActivity(intent2);
                return true;
            }
            return true;
        });

        ImageButton plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the InputNameFragment as a dialog
                InputNameFragment dialogFragment = new InputNameFragment();
                dialogFragment.show(getSupportFragmentManager(), "InputNameDialog");
            }
        });


    }


}