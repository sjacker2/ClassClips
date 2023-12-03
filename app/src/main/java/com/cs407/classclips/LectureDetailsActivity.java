package com.cs407.classclips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class LectureDetailsActivity extends AppCompatActivity {

    private int lectureId;
    private int classId;
    private Lecture lecture;
    private DBHelper dbHelper;

    // audio recording https://developer.android.com/guide/topics/media/platform/mediarecorder
    // **TO TEST ON THE EMULATOR** - make sure you enable 'virtual microphone uses host audio input' in the emulator settings
    private String filePath; // recording_<classid>_<lecture_id>
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder = null;
    boolean recording = false;
    private MediaPlayer player = null;
    boolean playing = false;

    SeekBar seekBar;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("testRecording", "prepare() failed");
        }

        recorder.start();
        recording = true;
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        recording = false;
    }

    private void startPlaying() {
        if (player == null) {
            player = new MediaPlayer();
            try {
                player.setDataSource(filePath);
                player.prepare();
            } catch (IOException e) {
                Log.e("playRecordingTest", "prepare() failed");
            }
        }

        // set max is crashing the app not sure why yet
        //seekBar.setMax(player.getDuration() / 1000);
        player.start();
        seekBar.setMax(player.getDuration() / 1000);
        Log.i("duration", "D: " + player.getDuration());
        playing = true;

        // https://stackoverflow.com/questions/17168215/seekbar-and-media-player-in-android
        Handler mHandler = new Handler();
        // update Seekbar on the UI thread
        LectureDetailsActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(player != null){
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

    }

    private void pauseAudio() {
        player.pause();
        playing = false;
    }

    private void stopPlaying() {
        player.release();
        player = null;
        playing = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        // Retrieve the lectureId passed from the previous activity
        lectureId = getIntent().getIntExtra("LECTURE_ID", -1);
        Log.d("LectureDetailsActivity", "Received lecture ID: " + lectureId);

        if (lectureId == -1) {
            Toast.makeText(this, "Error: Lecture not found.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if lectureId is not found
            return;
        }

        // get class id (ensure recording names are unique by combining class and lecture id)
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (lectureId == -1) {
            Toast.makeText(this, "Error: class id not found.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if class id is not found
            return;
        }

        // init the file path for this lecture's recording
        filePath = getExternalCacheDir().getAbsolutePath();
        filePath += "/recording_" + classId + "_" + lectureId + ".3gp";

        // request audio recording perms
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

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

        Button recordButton = findViewById(R.id.recordButton);
        Button playButton = findViewById(R.id.playButton);
        Button restartButton = findViewById(R.id.restartButton);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    endLecture();
                    recordButton.setText("Start Recording");
                } else {
                    startLecture();
                    recordButton.setText("Stop Recording");
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    pauseAudio();
                    playButton.setText("Play");
                } else {
                    playAudio();
                    playButton.setText("Pause");
                }

            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                playButton.setText("Play");
                seekBar.setProgress(0);
            }
        });

        seekBar = findViewById(R.id.seekBar);

        // setup seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                //seekBarHint.setVisibility(View.VISIBLE); // to show timestamp when dragging, will implement later
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //seekBarHint.setVisibility(View.VISIBLE);
                if(player != null && fromTouch){
                    player.seekTo(progress * 1000);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        startRecording();
    }

    private void endLecture() {
        // Code to handle ending the lecture
        //where end recording
        stopRecording();
    }

    private void playAudio() {
        startPlaying();
    }

    private void deleteLecture(int lectureId){
        //create alert dialog like how deleting class to check
        //if user wants to delete
        //if want to delete call dbhelper method deleteLecture(lectureId)
    }
}