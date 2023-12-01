package com.cs407.classclips;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import android.content.Context;


import android.widget.TextView;


import java.util.ArrayList;


public class ClassAdapter extends ArrayAdapter<Class> {
    private Context context;
    private ArrayList<Class> classes;

    public interface ClassAdapterListener {
        void onRenameClicked(Class classToRename, int position);
    }

    private ClassAdapterListener listener;

    public ClassAdapter(Context context, ArrayList<Class> classes, ClassAdapterListener listener) {
        super(context, R.layout.list_item_class, classes);
        this.context = context;
        this.classes = classes;
        this.listener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_class, parent, false);
        }

        TextView textViewClassName = convertView.findViewById(R.id.textViewClassName); // ERROR 1 Cannot resolve symbol 'TextView'
        Button buttonRename = convertView.findViewById(R.id.buttonRename);
        Button buttonDelete = convertView.findViewById(R.id.deleteClass);

        Class classItem = classes.get(position);
        textViewClassName.setText(String.format("Class: %s", classItem.getTitle())); // ERROR 2 Cannot resolve method 'setText(String)'

        buttonRename.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRenameClicked(classItem, position);
            }
        });

        buttonDelete.setOnClickListener(v -> {
            classes.remove(position);
            notifyDataSetChanged();
        });






        return convertView;
    }
}
