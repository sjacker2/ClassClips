package com.cs407.classclips;

import android.content.ContentValues;
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

    public static void createLecturePhotosTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS lecture_photos " +
                "(id INTEGER PRIMARY KEY, lectureId INTEGER, photoPath TEXT, FOREIGN KEY(lectureId) REFERENCES lectures(id))");
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

    public int saveClass(String username, String title) {
        createTable();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("title", title);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = sqLiteDatabase.insert("classes", null, values);
        return (int) newRowId;
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

    public int addLecture(int classId, String title) {
        createLectureTable();
        ContentValues values = new ContentValues();
        values.put("classId", classId);
        values.put("title", title);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = sqLiteDatabase.insert("lectures", null, values);
        return (int) newRowId;
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

    public void savePhotoPath(int lectureId, String photoPath) {
        createLecturePhotosTable();
        sqLiteDatabase.execSQL("INSERT INTO lecture_photos (lectureId, photoPath) VALUES (?, ?)",
                new String[]{String.valueOf(lectureId), photoPath});
    }

    public ArrayList<String> getPhotoPathsForLecture(int lectureId) {
        createLecturePhotosTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT photoPath FROM lecture_photos WHERE lectureId = ?",
                new String[]{String.valueOf(lectureId)});

        ArrayList<String> photoPaths = new ArrayList<>();
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndex("photoPath"));
            photoPaths.add(path);
        }
        c.close();
        return photoPaths;
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
