package com.cs407.classclips;

public class Lecture {
    private int id;
    private String lectureTitle;

    public Lecture(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
    public String getTitle() {
        return lectureTitle;
    }

}
