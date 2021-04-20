package com.example.todoapp.object;

public class Note {
    private int userId;
    private int id;
    private String title;
    private boolean completed;

    public Note(int userId,int id,String title,boolean completed){
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    //setters
    public int getUserId() {return userId;}
    public int getId() {return id;}
    public String getTitle() {return title;}
    public boolean isCompleted() {return completed;}

    //getters
    public void setUserId(int userId) { this.userId = userId; }
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; };
    public void setCompleted(boolean completed) { this.completed = completed; }
}
