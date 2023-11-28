package com.cs407.classclips;

public class Class {

    private String username;
    private String title;
    private int id;

    public Class(String username, String title) {
        this.username = username;
        this.title = title;
    }
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public String getTitle() {
        return title;
    }

}
