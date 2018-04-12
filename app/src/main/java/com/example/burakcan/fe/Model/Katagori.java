package com.example.burakcan.fe.Model;

public class Katagori {
    private String Name,Image;

    public Katagori() {
    }

    public Katagori(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
