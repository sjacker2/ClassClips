package com.cs407.classclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.navigation.NavigationBarView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();

        //for navigation bar w/ home, back, help
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if(itemId == R.id.nav_back){
                // navigate to previous screen
                finish();
                return true;
            }else if(itemId == R.id.nav_home){
                // navigate to welcome activity
                selectedFragment = new ClassesPageFragment();
            }else if(itemId == R.id.nav_help){
                // go to help screen
                selectedFragment = new HelpFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selectedFragment).commit();
            }

            return true;
        });

        //right now only shows up when refresh page, may need to
        //put this in method that is called when 'save' button is clicked
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new ClassesPageFragment()).commit();

    }

}