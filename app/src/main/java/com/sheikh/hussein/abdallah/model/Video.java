package com.sheikh.hussein.abdallah.model;

import java.io.Serializable;

public class Video implements Serializable {
    public String VideoId;
    public String Name;
    public String URL;
    public String Image;
    public String Duration;
    public String Description;
    public Boolean Featured;
    public String CreatedOn ;
    public long last_update = -1;
    public int list_type = 1;

    public Video(){

    }

    public Video(String image, String url){
        this.Image = image;
        this.URL = url;
    }

    public Video(String videoId){
        this.VideoId = videoId;
    }

}
