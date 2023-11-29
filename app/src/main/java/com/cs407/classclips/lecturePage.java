package com.cs407.classclips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class lecturePage extends AppCompatActivity {
    private int classId;
    private ArrayList<Lecture> lectures;
    private ArrayList<String> displayLectures;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_page);

        classId = getIntent().getIntExtra("CLASS_ID", -1);
        TextView classNameTextView = findViewById(R.id.classNameTextView);
        // TODO: Implement getting and setting the class name

        //for navigation bar with home, back, help
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
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

        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("classes", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase, context);

        // Initialize the displayLectures list and the adapter
        displayLectures = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayLectures);
        ListView lecturesListView = findViewById(R.id.lecturesListView);
        lecturesListView.setAdapter(adapter);

        // Set the item click listener for the ListView
        lecturesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lecture selectedLecture = lectures.get(position);
                openLectureDetail(selectedLecture);
            }
        });

        // Set the listeners for the Add and Delete buttons
        Button addLectureButton = findViewById(R.id.addLectureButton);
        addLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewLecture(classId);
            }
        });

        Button deleteClassButton = findViewById(R.id.deleteClassButton);
        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClass(classId);
            }
        });

        // Load the initial list of lectures
        loadLectures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLectures(); // Reload lectures to refresh the list
    }

    private void loadLectures() {
        // Fetch lectures for the given class from the database
        lectures = dbHelper.getLecturesForClass(classId);

        // Clear the existing data in displayLectures and repopulate it with the new data
        displayLectures.clear();
        for (Lecture lecture : lectures) {
            displayLectures.add(String.format("Lecture: %s", lecture.getTitle()));
        }

        // Notify the adapter of the data change to update the ListView
        adapter.notifyDataSetChanged();
    }


    private void openLectureDetail(Lecture lecture) {
        // Navigate to Lecture Detail Activity
        int lectureId = lecture.getId();

    }

    private void addNewLecture(int classId) {
        // Instantiate the InputNameFragment for adding a lecture
        InputNameFragment dialogFragment = InputNameFragment.newInstance(InputNameFragment.TYPE_LECTURE, classId);
        dialogFragment.show(getSupportFragmentManager(), "AddLectureDialog");
        loadLectures();
    }

    private void deleteClass(int classId) {
        // Delete class
        //go to welcome screen
    }

    private void deleteLecture(int lectureId){
        //put this in lecture details activity
    }
}