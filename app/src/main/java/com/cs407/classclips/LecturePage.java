package com.cs407.classclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.util.Log;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class LecturePage extends AppCompatActivity {
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
        TextView classNameTextView = findViewById(R.id.lectureNameTextView);
        // TODO: Implement getting and setting the class name

        //for navigation bar with home, back, help
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if(itemId == R.id.nav_back){
                // navigate to welcome activity
                Intent intent=new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }else if(itemId == R.id.nav_home){
                // navigate to welcome activity
                Intent intent=new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
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
                int lectid = selectedLecture.getId();
                openLectureDetail(lectid);
            }
        });

        // Set the listeners for the Add and Delete buttons
        Button addLectureButton = findViewById(R.id.buttonEndLecture);
        addLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewLecture(classId);
            }
        });

        SearchView searchView2 = findViewById(R.id.searchView2);

        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        Button deleteClassButton = findViewById(R.id.buttonStartLecture);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);
        int itemId = item.getItemId();

        if (itemId == R.id.logout) {
            sharedPreferences.edit().clear().apply();

            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filter(String text) {
        ArrayList<String> filteredText = new ArrayList<>();
        for (String lecture : displayLectures) {
            if (lecture.toLowerCase().contains(text.toLowerCase())) {
                filteredText.add(lecture);
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredText);
        ListView lecturesListView = findViewById(R.id.lecturesListView);
        lecturesListView.setAdapter(adapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadLectures(); // Reload lectures to refresh the list
    }

    private void addNewLecture(int classId) {
        // Instantiate the InputNameFragment for adding a lecture
        InputNameFragment dialogFragment = InputNameFragment.newInstance(InputNameFragment.TYPE_LECTURE, classId);
        dialogFragment.setOnLectureAddedListener(new InputNameFragment.OnLectureAddedListener() {
            @Override
            public void onLectureAdded(String lectureTitle) {
                int newLectureId = dbHelper.addLecture(classId, lectureTitle);
                openLectureDetail(newLectureId);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "AddLectureDialog");
        //loadLectures();
    }

    private void openLectureDetail(int lectureId) {
        // Navigate to Lecture Detail Activity
        Log.d("lecturePage", "Opening lecture details for ID: " + lectureId);
        Intent intent = new Intent(this, LectureDetailsActivity.class);
        intent.putExtra("LECTURE_ID", lectureId);
        intent.putExtra("CLASS_ID", classId);
        startActivity(intent);
    }


    //checks to make sure user wants to delete class
    private void deleteClass(int classId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class and all its lectures?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        performClassDeletion(classId);
                    }})
                .setNegativeButton("Cancel", null).show();
    }

    //deletes the class and all of the lectures inside
    private void performClassDeletion(int classId) {
        dbHelper.deleteClass(classId);

        // Navigate to the Welcome Screen or Main Screen
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }
}