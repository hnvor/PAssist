package com.example.wilshere.voicerecognitionactivity;

public class Task {
    String name, start_time,end_time;
    int image,id;

    public Task(){
    }

    Task(String _describe, String _start_time, String _end_time, int _image,int _id) {
        start_time = _start_time;
        end_time = _end_time;
        name = _describe;
        image = _image;
        id = _id;
    }

    public String getName(){
        return name;
    }

    public String getStart_time(){
        return start_time;
    }

    public String getEnd_time(){
        return end_time;
    }

    public int getImage(){
        return image;
    }

    public int getId() {
        return id;
    }

   /////


    public void setName(String name) {
        this.name = name;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }
}
