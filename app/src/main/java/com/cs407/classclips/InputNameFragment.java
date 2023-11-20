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

//this is the fragment that brings up the add a class popup
public class InputNameFragment extends DialogFragment {
    private EditText editTextName;
    private Button buttonSave;
    private Button buttonCancel;

    SharedPreferences sharedPreferences;
    DBHelper dbHelper;

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = editTextName.getText().toString().trim();
                if (!className.isEmpty()) {
                    // Save the class name
                    String username = sharedPreferences.getString("username", "");
                    dbHelper.saveClass(username, className);

                    Toast.makeText(getActivity(), "Name saved!", Toast.LENGTH_SHORT).show();

                    dismiss(); // Close the dialog
                } else {
                    Toast.makeText(getActivity(), "Please enter a class name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the dialog without saving
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
