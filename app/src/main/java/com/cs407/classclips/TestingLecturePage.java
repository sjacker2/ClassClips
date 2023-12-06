package com.cs407.classclips;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TestingLecturePage extends AppCompatActivity {

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

    private String currentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSIONS = 201;
    private static final int REQUEST_CAMERA_PERMISSION = 202;
    private static final int REQUEST_STORAGE_PERMISSION = 203;
    private boolean permissionToRecordAccepted = false;
    private boolean cameraPermissionGranted = false;
    private boolean writeExternalStoragePermissionGranted = false;

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionToRecordAccepted = true;
                } else {
                    Toast.makeText(this, "Permission for audio recording denied", Toast.LENGTH_SHORT).show();
                    // Handle the denial as appropriate for your app
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeExternalStoragePermissionGranted = true;
                } else {
                    Toast.makeText(this, "Permission for external storage denied", Toast.LENGTH_SHORT).show();
                    // Handle the denial as appropriate for your app
                }
                break;
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionGranted = true;
                } else {
                    Toast.makeText(this, "Permission for camera denied", Toast.LENGTH_SHORT).show();
                    // Handle the denial as appropriate for your app
                }
                break;
        }
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
        TestingLecturePage.this.runOnUiThread(new Runnable() {

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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERMISSION);

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


        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Save each new photo path in the database
                            dbHelper.savePhotoPath(lectureId, currentPhotoPath);
                            // Optionally, refresh the list view to display the new photo
                            loadPhotoList();
                        }
                    }
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

        Button snapPicButton = findViewById(R.id.snapPicButton);
        snapPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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

        //load photos list
        loadPhotoList();
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

    private void loadPhotoList() {
        ArrayList<String> photoPaths = dbHelper.getPhotoPathsForLecture(lectureId);
        ArrayList<String> photoNames = new ArrayList<>();
        for (String path : photoPaths) {
            File photoFile = new File(path);
            photoNames.add(photoFile.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, photoNames);
        ListView photosListView = findViewById(R.id.photosListView);
        photosListView.setAdapter(adapter);

        photosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showImageInPopup(photoPaths.get(position));
            }
        });
    }

    private void showImageInPopup(String photoPath) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_view);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        Bitmap myBitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(myBitmap);

        dialog.show();
    }



    private void dispatchTakePictureIntent() {
        // Check if the necessary permissions have been granted
        if (!cameraPermissionGranted || !writeExternalStoragePermissionGranted) {
            Toast.makeText(this, "Camera and storage permissions are required.", Toast.LENGTH_SHORT).show();
            // Optionally, you could re-request the permissions here
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error, e.g., by logging or displaying a message
                Log.e("LectureDetailsActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.cs407.classclips.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
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