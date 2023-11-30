package com.cs407.classclips;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    static Context context;
    static SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase, Context context) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.context = context;
    }

    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS classes " +
                "(id INTEGER PRIMARY KEY, classId INTEGER, username TEXT, title TEXT)");
    }

    public static void createLectureTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS lectures " +
                "(id INTEGER PRIMARY KEY, classId INTEGER, title TEXT, FOREIGN KEY(classId) REFERENCES classes(id))");
    }



    public ArrayList<Class> readClasses(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM classes WHERE username LIKE ?",
                new String[]{username});
        int titleIndex = c.getColumnIndex("title");
        int idIndex = c.getColumnIndex("id");
        c.moveToFirst();

        ArrayList<Class> classList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            int classId = c.getInt(idIndex);

            Class aClass = new Class(username, title);
            aClass.setId(classId);
            classList.add(aClass);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return classList;
    }

    public void saveClass(String username, String title) {
        createTable();

        sqLiteDatabase.execSQL("INSERT INTO classes (username, title) VALUES (?, ?)",
                new String[]{username, title});
    }

    public ArrayList<Lecture> getLecturesForClass(int classId) {
        createLectureTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM lectures WHERE classId = ?",
                new String[]{String.valueOf(classId)});
        int idIndex = c.getColumnIndex("id"); // Get the index of the id column
        int titleIndex = c.getColumnIndex("title"); // Get the index of the title column

        ArrayList<Lecture> lecturesList = new ArrayList<>();
        while (c.moveToNext()) {
            int lectureId = c.getInt(idIndex); // Fetch the lecture id
            String title = c.getString(titleIndex); // Fetch the lecture title
            Lecture lecture = new Lecture(title);
            lecture.setId(lectureId); // Set the lecture id in the Lecture object (assuming you have setId method in Lecture class)
            lecturesList.add(lecture);
        }
        c.close();
        return lecturesList;
    }

    public void addLecture(int classId, String title) {
        createLectureTable();
        sqLiteDatabase.execSQL("INSERT INTO lectures (classId, title) VALUES (?, ?)",
                new String[]{String.valueOf(classId), title});
    }


    public void updateClass(String content, String username, String title) {
        createTable();

        Class aClass = new Class(username, title);
        sqLiteDatabase.execSQL("UPDATE classes set content=? where title=? and username=?",
                new String[] {content, title, username});
    }

    public void deleteClass(int classId) {
        // First delete all lectures associated with the class
        deleteLecturesForClass(classId);

        // Now delete the class itself
        sqLiteDatabase.execSQL("DELETE FROM classes WHERE id = ?", new String[]{String.valueOf(classId)});
    }

    public void deleteLecturesForClass(int classId) {
        createLectureTable();
        sqLiteDatabase.execSQL("DELETE FROM lectures WHERE classId = ?",
                new String[]{String.valueOf(classId)});
    }

    public Lecture getLectureById(int lectureId) {
        createLectureTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM lectures WHERE id = ?", new String[]{String.valueOf(lectureId)});

        Lecture lecture = null;
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");

            int id = c.getInt(idIndex);
            String title = c.getString(titleIndex);

            lecture = new Lecture(title);
            lecture.setId(id);
        }
        c.close();
        return lecture;
    }


    public void deleteLecture(int lectureId) {
        createLectureTable();
        sqLiteDatabase.execSQL("DELETE FROM lectures WHERE id = ?", new String[]{String.valueOf(lectureId)});
    }

    public void updateClassName(int classId, String newTitle) {
        createTable();
        sqLiteDatabase.execSQL("UPDATE classes SET title = ? WHERE id = ?",
                new String[]{newTitle, String.valueOf(classId)});
    }
}
