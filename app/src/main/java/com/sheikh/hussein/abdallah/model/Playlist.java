package com.sheikh.hussein.abdallah.model;

import java.io.Serializable;

public class Playlist implements Serializable {
    public String CategoryId = "";
    public String Name;
    public String Image;
    public String Description;
    public int NumberOfVideos;
    public Boolean Featured = false;
    public String CreatedOn;

    public Playlist() {
    }

    public Playlist(String id, String name) {
        this.CategoryId = id;
        this.Name = name;
    }

    public Playlist(String id) {
        this.CategoryId = id;
    }

}
