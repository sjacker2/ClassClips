package com.cs407.classclips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassesPageFragment#newInstance} factory method to
 * create an instance of this fragment
 */
public class ClassesPageFragment extends Fragment implements ClassAdapter.ClassAdapterListener, InputNameFragment.OnClassAddedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> displayClasses;
    static ArrayList<Class> classes;
    private ClassAdapter adapter; // Changed from ArrayAdapter<String> to ClassAdapter
    private DBHelper dbHelper;

    public ClassesPageFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassesPageFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassesPageFragment newInstance(String param1, String param2) {
        ClassesPageFragment fragment = new ClassesPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classes_page_fragement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ImageButton plusButton = view.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate the InputNameFragment with type for adding a class
                InputNameFragment dialogFragment = InputNameFragment.newInstance(InputNameFragment.TYPE_CLASS, -1); // -1 as classId since it's irrelevant here
                dialogFragment.setListener(ClassesPageFragment.this); // Set the listener
                dialogFragment.show(getActivity().getSupportFragmentManager(), "InputNameDialog");
            }
        });
        //
        //  Search for individual classes
        //
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Class> filteredList = new ArrayList<>();
                for (Class classItem : classes) {
                    if(classItem.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(classItem);
                    }
                }

                adapter = new ClassAdapter(getActivity(), filteredList, ClassesPageFragment.this);
                ListView listView = view.findViewById(R.id.classesListView);

                listView.setAdapter(adapter);
                return true;
            }
        });

        Context context = getActivity().getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("classes", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase, getActivity().getApplicationContext());


        // current bug: when you add a new class the list does not visually update until you navigate to help and back to home.
        initClasses(view);

    }
    //fixes classes not showing bug hopefully
    @Override
    public void onResume() {
        super.onResume();
        initClasses(getView());
    }

    @Override
    public void onClassAdded(int classId) {
        openLecturePage(classId); // Open the lecture page for the newly added class
    }

    @Override
    public void onRenameClicked(Class classToRename, int position) {
        showRenameDialog(classToRename, position);
    }

    public void initClasses(View view) {
        // get data from database
        Context context = getActivity().getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("classes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase, getActivity().getApplicationContext());
        displayClasses = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);

        String user = sharedPreferences.getString("username", "");

        classes = dbHelper.readClasses(user);

        //strings to display
        for (Class classItem : classes) {
            displayClasses.add(String.format("Class: %s", classItem.getTitle()));
        }

        ListView listView = view.findViewById(R.id.classesListView);
        this.adapter = new ClassAdapter(getActivity(), classes, this);
        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            int selectedClassId = classes.get(position).getId();
            openLecturePage(selectedClassId);
        });

        listView.setOnItemLongClickListener((parent, view12, position, id) -> {
            showRenameDialog(classes.get(position), position);
            return true;
        });
    }

    private void showRenameDialog(Class classToRename, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename Class");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(classToRename.getTitle());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newClassName = input.getText().toString();
            updateClassName(classToRename, newClassName, position);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateClassName(Class classToRename, String newClassName, int position) {
        DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext().openOrCreateDatabase("classes", Context.MODE_PRIVATE, null), getActivity().getApplicationContext());
        dbHelper.updateClassName(classToRename.getId(), newClassName);

        // Update the class in the ArrayList and refresh the ListView
        classToRename.setTitle(newClassName);
        displayClasses.set(position, String.format("Class: %s", newClassName));
        this.adapter.notifyDataSetChanged();

        // Reload classes from the database and update the adapter
        loadClasses();
    }

    private void loadClasses() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.classclips", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username", "");
        classes = dbHelper.readClasses(user);

        // Update the adapter with the new list of classes
        this.adapter = new ClassAdapter(getActivity(), classes, this);
        ListView listView = getView().findViewById(R.id.classesListView);
        listView.setAdapter(this.adapter);
    }


    //when click on a class goes to that class page
    private void openLecturePage(int classId) {
        Intent intent = new Intent(getActivity().getApplicationContext(), LecturePage.class);
        intent.putExtra("CLASS_ID", classId);
        startActivity(intent);
    }



}