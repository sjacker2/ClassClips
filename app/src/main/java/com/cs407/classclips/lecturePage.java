package com.cs407.classclips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class lecturePage extends AppCompatActivity {
    private int classId;
    private ArrayList<Lecture> lectures; // Replace Lecture with your lecture model class
    private ArrayAdapter<Lecture> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_page);

        // Get class name from intent
        classId = getIntent().getIntExtra("CLASS_ID", -1);

        TextView classNameTextView = findViewById(R.id.classNameTextView);
        //need to implement getting class name
        //classNameTextView.setText(className);

        ListView lecturesListView = findViewById(R.id.lecturesListView);
        // Initialize your lectures list
        lectures = new ArrayList<>();
        // Create an adapter and set it to the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lectures);
        lecturesListView.setAdapter(adapter);

        // Load lectures from the database
        loadLectures(classId);

        lecturesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open Lecture Detail Activity
                openLectureDetail(lectures.get(position));
            }
        });

        Button addLectureButton = findViewById(R.id.addLectureButton);
        addLectureButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding a new lecture
                addNewLecture(classId);
            }
        });

        Button deleteClassButton = findViewById(R.id.deleteClassButton);
        deleteClassButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deleting the class
                deleteClass(classId);
            }
        });

    }

    private void loadLectures(int classId) {
        // Fetch lectures for the given class from the database and update 'lectures' list
    }

    private void openLectureDetail(Lecture lecture) {
        // Navigate to Lecture Detail Activity
        int lectureId = lecture.getId();

    }

    private void addNewLecture(int classId) {
        // Add new lecture logic
    }

    private void deleteClass(int classId) {
        // Delete class
        //go to welcome screen
    }

    private void deleteLecture(int lectureId){

    }
}