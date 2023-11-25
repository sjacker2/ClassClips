package com.cs407.classclips;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassesPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassesPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> displayClasses;
    static ArrayList<Class> classes;

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
                // Show the InputNameFragment as a dialog
                InputNameFragment dialogFragment = new InputNameFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager(), "InputNameDialog");
            }
        });

        // current bug: when you add a new class the list does not visually update until you navigate to help and back to home.
        initClasses(view);

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

        for (Class classItem : classes) {
            displayClasses.add(String.format("Class: %s", classItem.getTitle()));
        }

        ListView listView = view.findViewById(R.id.classesListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, displayClasses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle click event, open new Fragment/Activity for the selected class
                String selectedClass = classes.get(position).getTitle();
                openLecturePage(selectedClass);
            }
        });

        // create strings for display and add to the tablelayout view
        /* for (Class note : classes) {
            displayClasses.add(String.format("Class: %s\n", note.getTitle()));
        }

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, displayClasses);
        TableLayout tableLayout = view.findViewById(R.id.classesTableLayout);

        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                tableLayout.addView(adapter.getView(i, null, tableLayout));
            }
        } */
    }

    private void openLecturePage(String className) {
        // Code to open new Fragment/Activity here
        // Pass className as an argument or extra to the new Fragment/Activity
    }

}