package com.cs407.classclips;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        Class classItem = classes.get(position);
        textViewClassName.setText(String.format("Class: %s", classItem.getTitle())); // ERROR 2 Cannot resolve method 'setText(String)'

        buttonRename.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRenameClicked(classItem, position);
            }
        });


        return convertView;
    }
}
