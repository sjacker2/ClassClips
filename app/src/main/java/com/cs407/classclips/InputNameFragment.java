package com.cs407.classclips;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

//this is the fragment that brings up the add a class or lecture popup
public class InputNameFragment extends DialogFragment {
    private EditText editTextName;
    private Button buttonSave;
    private Button buttonCancel;

    SharedPreferences sharedPreferences;
    DBHelper dbHelper;

    public static final int TYPE_CLASS = 0;
    public static final int TYPE_LECTURE = 1;

    private int type; // This determines if the fragment is used for a class or a lecture
    private int classId; // Needed if this is for adding a lecture

    public static InputNameFragment newInstance(int type, int classId) {
        InputNameFragment fragment = new InputNameFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("classId", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
            classId = getArguments().getInt("classId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_class_fragment, null);

        editTextName = view.findViewById(R.id.editTextName);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        sharedPreferences = getActivity().getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);
        Context context = getActivity().getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("classes", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase, getActivity().getApplicationContext());

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the dialog without saving
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                if (!name.isEmpty()) {
                    if (type == TYPE_CLASS) {
                        // Save the class name
                        String username = sharedPreferences.getString("username", "");
                        dbHelper.saveClass(username, name);
                        Toast.makeText(getActivity(), "Class saved!", Toast.LENGTH_SHORT).show();
                    } else if (type == TYPE_LECTURE) {
                        // Save the lecture name
                        dbHelper.addLecture(classId, name);
                        Toast.makeText(getActivity(), "Lecture saved!", Toast.LENGTH_SHORT).show();
                    }

                    dismiss(); // Close the dialog
                } else {
                    Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
