package com.cs407.classclips;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;
    static Context context;


    public DBHelper(SQLiteDatabase sqLiteDatabase, Context context) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.context = context;
    }

    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS classes " +
                "(id INTEGER PRIMARY KEY, classId INTEGER, username TEXT, title TEXT)");
    }

    public ArrayList<Class> readClasses(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM classes WHERE username LIKE ?",
                new String[]{username});
        int titleIndex = c.getColumnIndex("title");
        c.moveToFirst();

        ArrayList<Class> notesList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);

            Class aClass = new Class(username, title);
            notesList.add(aClass);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return notesList;
    }

    public void saveClass(String username, String title) {
        createTable();

        sqLiteDatabase.execSQL("INSERT INTO classes (username, title) VALUES (?, ?)",
                new String[]{username, title});
    }

    public void updateClass(String content, String username, String title) {
        createTable();

        Class aClass = new Class(username, title);
        sqLiteDatabase.execSQL("UPDATE classes set content=? where title=? and username=?",
                new String[] {content, title, username});
    }

    public void deleteClass(String username, String title) {
        createTable();

        sqLiteDatabase.execSQL("DELETE FROM notes WHERE username = ? AND title=?",
                new String[] {username, title});
    }

}
