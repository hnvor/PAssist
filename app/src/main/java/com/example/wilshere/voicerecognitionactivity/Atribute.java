package com.example.wilshere.voicerecognitionactivity;

public class Atribute {
    String name, value;
    int image,color;

    public Atribute(){}

    public Atribute(String name, String value, int image,int color) {
        this.name = name;
        this.value = value;
        this.image = image;
        this.color = color;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
