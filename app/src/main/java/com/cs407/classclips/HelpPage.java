package com.cs407.classclips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HelpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if(itemId == R.id.nav_back){
                finish();
                return true;
            }else if(itemId == R.id.nav_home){
                // navigate to welcome activity
                Intent intent=new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }else if(itemId == R.id.nav_help){
                return true;
            }

            return true;
        });
    }
}