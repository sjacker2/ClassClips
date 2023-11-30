package com.cs407.classclips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LectureDetailsActivity extends AppCompatActivity {

    private int lectureId;
    private Lecture lecture;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        // Retrieve the lectureId passed from the previous activity
        lectureId = getIntent().getIntExtra("LECTURE_ID", -1);
        if (lectureId == -1) {
            Toast.makeText(this, "Error: Lecture not found.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if lectureId is not found
            return;
        }

        // Initialize the DBHelper
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("classes", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase, context);

        // Load lecture details from the database
        lecture = dbHelper.getLectureById(lectureId);
        if (lecture == null) {
            Toast.makeText(this, "Lecture details could not be loaded.", Toast.LENGTH_LONG).show();
            return;
        }

        //for navigation bar with home, back, help
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);
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
                // navigate to help screen
                selectedFragment = new HelpFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, selectedFragment).commit();
            }

            return true;
        });

        // Initialize UI components with lecture details
        initializeUI();

        Button startLectureButton = findViewById(R.id.buttonStartLecture);
        Button endLectureButton = findViewById(R.id.buttonEndLecture);

        startLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLecture();
            }
        });

        endLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLecture();
            }
        });
    }


    private void initializeUI() {
        //set up UI
        TextView lectureNameTextView = findViewById(R.id.lectureNameTextView);
        lectureNameTextView.setText(lecture.getTitle());
        //maybe add date here too

        //load recordings and pictures if already have some
    }

    private void startLecture() {
        // Code to handle starting the lecture
        //where start recording
    }

    private void endLecture() {
        // Code to handle ending the lecture
        //where end recording
    }

    private void deleteLecture(int lectureId){
        //create alert dialog like how deleting class to check
        //if user wants to delete
        //if want to delete call dbhelper method deleteLecture(lectureId)
    }
}